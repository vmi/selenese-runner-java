package jp.vmi.selenium.selenese.command;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import jp.vmi.selenium.testutils.TestBase;

import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.webdriver.WebDriverManager;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * test for {@link Assertion}.
 */
@RunWith(Parameterized.class)
public class AssertionStringMatchTest extends TestBase {

    private final String commandName;
    private final String[] argument;
    private final String assertionType;
    private final String getter;
    private final boolean isBoolean;
    private final boolean isInverse;
    private final Class<? extends Result> resultClass;

    /**
     * Construct testcase by parameters.
     *
     * @param commandName command name
     * @param argument selenese command argument
     * @param assertionType assertion type (assert, verify)
     * @param getter getter name
     * @param isBoolean true if boolean command
     * @param isInverse true if NOT command
     * @param resultClass expected result class
     */
    public AssertionStringMatchTest(String commandName, String[] argument, String assertionType, String getter, boolean isBoolean,
        boolean isInverse, Class<? extends Result> resultClass) {
        super();
        this.commandName = commandName;
        this.argument = argument;
        this.assertionType = assertionType;
        this.getter = getter;
        this.isBoolean = isBoolean;
        this.isInverse = isInverse;
        this.resultClass = resultClass;
    }

    /**
     * Provide Test Parameters
     * @return test parameters
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "assertTitle", new String[] { "ti*" }, "assert", "getTitle", false, false, Success.class }
            , { "assertTitle", new String[] { "glob:ti*" }, "assert", "getTitle", false, false, Success.class }
            , { "assertTitle", new String[] { "regexp:ti.+" }, "assert", "getTitle", false, false, Success.class }
            , { "assertTitle", new String[] { "regexpi:TI.+" }, "assert", "getTitle", false, false, Success.class }
            , { "assertTitle", new String[] { "exact:title" }, "assert", "getTitle", false, false, Success.class }
            , { "assertTitle", new String[] { "ti*l" }, "assert", "getTitle", false, false, Failure.class }
            , { "assertTitle", new String[] { "glob:ti*l" }, "assert", "getTitle", false, false, Failure.class }
            , { "assertTitle", new String[] { "regexp:ti.+l$" }, "assert", "getTitle", false, false, Failure.class }
            , { "assertTitle", new String[] { "regexpi:TI.+l$" }, "assert", "getTitle", false, false, Failure.class }
            , { "assertTitle", new String[] { "exact:titl" }, "assert", "getTitle", false, false, Failure.class }
            , { "assertNotTitle", new String[] { "ti*" }, "assert", "getTitle", false, true, Failure.class }
            , { "assertNotTitle", new String[] { "glob:ti*" }, "assert", "getTitle", false, true, Failure.class }
            , { "assertNotTitle", new String[] { "regexp:ti.+" }, "assert", "getTitle", false, true, Failure.class }
            , { "assertNotTitle", new String[] { "regexpi:TI.+" }, "assert", "getTitle", false, true, Failure.class }
            , { "assertNotTitle", new String[] { "exact:title" }, "assert", "getTitle", false, true, Failure.class }
            // for issue 56
            // FIXME: The following command does not work on "selenium-2.32.0".
            // , { "assertText", new String[] { "test", "something*" }, "assert", "getText", false, false, Success.class }
        });
    }

    /**
     * test for string-match
     *
     * @see "http://release.seleniumhq.org/selenium-core/1.0.1/reference.html#patterns"
     *
     * @throws IOException exception.
     */
    @Test
    public void stringMatchPattern() throws IOException {
        Command open = new Open(1, "open", new String[] { "file://"
            + TestUtils.getScriptFile(AssertionStringMatchTest.class, "") },
            "open", true);
        Assertion assertionDefaultGlob = new Assertion(1, commandName, argument, assertionType, getter, isBoolean,
            isInverse);
        File selenesefile = File.createTempFile("selenese", ".html");

        TestCase testcase = new TestCase();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        Runner runner = new Runner();
        runner.setDriver(wdm.get());
        testcase.initialize(selenesefile.getPath(), "test", runner, ws.getUrl());

        assertTrue(open.doCommand(testcase, runner).isSuccess());

        assertThat(assertionDefaultGlob.doCommand(testcase, runner).getMessage(), assertionDefaultGlob.doCommand(testcase, runner),
            is(instanceOf(resultClass)));
    }

}
