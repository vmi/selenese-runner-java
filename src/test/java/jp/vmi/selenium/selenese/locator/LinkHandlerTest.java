package jp.vmi.selenium.selenese.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.webdriver.DriverOptions;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * test for {@link LinkHandler}.
 */
@SuppressWarnings("javadoc")
@RunWith(Parameterized.class)
public class LinkHandlerTest extends TestBase {

    private static WebDriver driver = null;

    private final String commandName;
    private final String argument;
    private final Class<? extends Result> resultClass;

    private static final Pattern RE = Pattern.compile("\\\\(.)", Pattern.DOTALL);

    private static String unquote(String s) {
        if (!s.contains("\\"))
            return s;
        StringBuilder b = new StringBuilder();
        Matcher m = RE.matcher(s);
        int f;
        for (f = 0; m.find(f); f = m.end()) {
            int t = m.start();
            if (f < t)
                b.append(s.substring(f, t));
            char ch = m.group(1).charAt(0);
            switch (ch) {
            case 't':
                ch = '\t';
                break;
            case 'r':
                ch = '\r';
                break;
            case 'n':
                ch = '\n';
                break;
            default:
                break;
            }
            b.append(ch);
        }
        if (f < s.length())
            b.append(s.substring(f));
        return b.toString();
    }

    /**
     * Construct testcase by parameters.
     *
     * @param factoryName WebDriver factory name
     * @param commandName command name
     * @param argument selenese command argument
     * @param resultClass expected result class
     */
    public LinkHandlerTest(String factoryName, String commandName, String argument, Class<? extends Result> resultClass) {
        if (!factoryName.equals(TestBase.factoryName)) {
            setWebDriverFactory(factoryName, new DriverOptions());
            try {
                driver = manager.get();
            } catch (UnreachableBrowserException e) {
                TestBase.factoryName = null;
                driver = null;
                Assume.assumeNoException(e);
            } catch (UnsupportedOperationException e) {
                TestBase.factoryName = null;
                driver = null;
                Assume.assumeNoException(e);
            }
        }
        this.commandName = commandName;
        this.argument = unquote(argument);
        this.resultClass = resultClass;
    }

    @Parameters(name = "{index}: {0} {1}({2}) => {3}")
    public static Collection<Object[]> data() {
        List<Object[]> factories = TestUtils.getWebDriverFactories();
        Object[][] argss = new Object[][] {
            { "assertElementPresent", "as*", Success.class },
            { "assertElementPresent", "glob:as*", Success.class },
            { "assertElementPresent", "regexp:as.+", Success.class },
            { "assertElementPresent", "regexpi:AS.+", Success.class },
            { "assertElementPresent", "exact:assertion test", Success.class },
            { "assertElementPresent", "exact:anchor with nested elements", Success.class },
            { "assertElementPresent", "glob:*with nested*", Success.class },
            { "assertElementPresent", "exact:not normalized spaces here", Success.class },
            { "assertElementPresent", "glob:*normalized spaces*", Success.class },
            { "assertElementPresent", "as*s", Failure.class },
            { "assertElementPresent", "glob:as*s", Failure.class },
            { "assertElementPresent", "regexp:as.+s$", Failure.class },
            { "assertElementPresent", "regexpi:AS.+S$", Failure.class },
            { "assertElementPresent", "exact:assertion", Failure.class },
            { "assertElementNotPresent", "as*", Failure.class },
            { "assertElementNotPresent", "glob:as*", Failure.class },
            { "assertElementNotPresent", "regexp:as.+", Failure.class },
            { "assertElementNotPresent", "regexpi:AS.+", Failure.class },
            { "assertElementNotPresent", "exact:assertion test", Failure.class }

        };
        ArrayList<Object[]> data = new ArrayList<>();
        for (Object[] factory : factories)
            for (Object[] args : argss)
                data.add(ArrayUtils.addAll(factory, args));
        return data;
    }

    @Test
    public void linkHandlerTest() throws IOException {
        Runner runner = new Runner();
        runner.setDriver(driver);
        CommandFactory cf = runner.getCommandFactory();
        TestCase testCase = Binder.newTestCase("dummy", "dummy", wsr.getBaseURL());
        testCase.addCommand(cf, "open", "/link_handler.html");
        testCase.addCommand(cf, commandName, "link=" + argument);
        Result result = runner.execute(testCase);
        assertThat(result, is(instanceOf(resultClass)));
    }
}
