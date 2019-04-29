package jp.vmi.selenium.runner.converter;

import java.util.List;
import java.util.Set;

import jp.vmi.selenium.runner.model.side.SideCommand;
import jp.vmi.selenium.runner.model.side.SideFile;
import jp.vmi.selenium.runner.model.side.SideSuite;
import jp.vmi.selenium.runner.model.side.SideTest;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.utils.EscapeUtils;
import jp.vmi.selenium.selenese.utils.LangUtils;
import jp.vmi.selenium.selenese.utils.SeleniumUtils.SeleniumPattern;

/**
 * Convert Selenese to Side format.
 */
@SuppressWarnings("javadoc")
public final class Converters {

    public static String SELENIUM_IDE_VERSION = "v3.4.4";

    private static final String AND_WAIT = "AndWait";

    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";

    private static void addMessage(List<String> messages, ICommand srcCmd, String level, String text) {
        String message = String.format("%s [%s] %s", srcCmd, level, text);
        messages.add(message);
    }

    private static void convertCommand(SideTest sideTest, ICommand srcCmd, List<String> messages) {
        SideCommand dstCmd = new SideCommand(true);
        String name = srcCmd.getName();
        String[] arguments = srcCmd.getArguments();

        // Conversion rules.

        // remove "AndWait" suffix.
        if (name.endsWith(AND_WAIT)) {
            name = name.substring(0, name.length() - AND_WAIT.length());
            addMessage(messages, srcCmd, INFO, "remove \"" + AND_WAIT + "\" suffix");
        }
        // screenshot command is not supported.
        if (LangUtils.containsIgnoreCase(name, "screenshot")) {
            StringBuilder echo = new StringBuilder("NotSupported: ").append(name).append('(');
            name = "echo";
            switch (arguments.length) {
            case 2:
                echo.append('"').append(EscapeUtils.escapeJSString(arguments[0]))
                    .append("\", \"").append(EscapeUtils.escapeJSString(arguments[1])).append('"');
                break;
            case 1:
                echo.append('"').append(EscapeUtils.escapeJSString(arguments[0])).append('"');
                break;
            case 0:
                break;
            default:
                throw new RuntimeException("Can't convert: " + srcCmd);
            }
            echo.append(')');
            arguments = new String[] { echo.toString() };
            addMessage(messages, srcCmd, WARN, "screenshot command is not supported.");
        }
        if ("assertText".equalsIgnoreCase(name)) {
            SeleniumPattern pattern = new SeleniumPattern(arguments[1]);
            switch (pattern.type) {
            case EXACT:
                arguments[1] = pattern.stringPattern;
                break;
            case GLOB:
            case REGEXP:
            case REGEXPI:
                addMessage(messages, srcCmd, ERROR, "pattern match is not supported (" + SELENIUM_IDE_VERSION + ")");
                break;
            }
        }

        dstCmd.setCommand(name);
        switch (arguments.length) {
        case 2:
            dstCmd.setValue(arguments[1]);
            // fall through.
        case 1:
            dstCmd.setTarget(arguments[0]);
            // fall through.
        case 0:
            break;
        default:
            throw new RuntimeException("Can't convert");
        }
        sideTest.addCommand(dstCmd);
    }

    public static SideTest convertTestCase(TestCase testCase, List<String> messages) {
        SideTest sideTest = new SideTest(true);
        sideTest.setName(testCase.getName());
        testCase.getCommandList().forEach(srcCmd -> convertCommand(sideTest, srcCmd, messages));
        return sideTest;
    }

    private static void addSingleTestCase(TestCase testCase, SideFile sideFile, List<String> messages) {
        sideFile.setUrl(testCase.getBaseURL());
        SideSuite sideSuite = new SideSuite(true);
        sideSuite.setName(testCase.getName());
        SideTest sideTest = convertTestCase(testCase, messages);
        sideFile.addTest(sideTest);
        sideSuite.addTest(sideTest);
        sideFile.addSuite(sideSuite);
    }

    public static void addTestSuite(TestSuite testSuite, SideFile sideFile, List<String> messags, Set<String> childFiles) throws SeleneseRunnerRuntimeException {
        List<Selenese> seleneseList = testSuite.getSeleneseList();
        if (!seleneseList.isEmpty()) {
            Selenese firstChild = seleneseList.get(0);
            if (!(firstChild instanceof TestCase))
                throw new SeleneseRunnerRuntimeException(firstChild.toString());
            String baseURL = ((TestCase) firstChild).getBaseURL();
            sideFile.setUrl(baseURL);
        }
        SideSuite sideSuite = new SideSuite(true);
        sideSuite.setName(testSuite.getName());
        seleneseList.forEach(selenese -> {
            if (selenese instanceof TestCase)
                throw new SeleneseRunnerRuntimeException(selenese.toString());
            TestCase testCase = (TestCase) selenese;
            SideTest sideTest = convertTestCase(testCase, messags);
            sideFile.addTest(sideTest);
            sideSuite.addTest(sideTest);
            childFiles.add(testCase.getFilename());
        });
        sideFile.getSuites().add(sideSuite);
    }

    public static SideFile convertSelenese(Selenese selenese, List<String> messages, Set<String> childFiles) {
        SideFile sideFile = new SideFile(true);
        sideFile.setName(selenese.getName());
        switch (selenese.getType()) {
        case TEST_CASE:
            TestCase testCase = (TestCase) selenese;
            if (childFiles.contains(testCase.getFilename()))
                return null;
            addSingleTestCase(testCase, sideFile, messages);
            break;
        case TEST_SUITE:
            TestSuite testSuite = (TestSuite) selenese;
            try {
                addTestSuite(testSuite, sideFile, messages, childFiles);
            } catch (SeleneseRunnerRuntimeException e) {
                System.out.println(e.getMessage());
                return null;
            }
            break;
        case TEST_PROJECT:
            throw new IllegalArgumentException(selenese.getName() + " is not selenese format.");

        }
        return sideFile;
    }
}
