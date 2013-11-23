package jp.vmi.selenium.selenese.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * test for {@link Assertion}.
 */
@RunWith(Parameterized.class)
public class AssertionStringMatchTest extends TestBase {

    private final String commandName;
    private final String argument;
    private final Class<? extends Result> resultClass;

    /**
     * Construct testcase by parameters.
     *
     * @param commandName command name
     * @param argument selenese command argument
     * @param resultClass expected result class
     */
    public AssertionStringMatchTest(String commandName, String argument, Class<? extends Result> resultClass) {
        this.commandName = commandName;
        this.argument = argument;
        this.resultClass = resultClass;
    }

    /**
     * Provide Test Parameters
     *
     * @return test parameters
     */
    @Parameters(name = "{index}: {0}({1}) => {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "assertTitle", "as*", Success.class }
            , { "assertTitle", "glob:as*", Success.class }
            , { "assertTitle", "regexp:as.+", Success.class }
            , { "assertTitle", "regexpi:AS.+", Success.class }
            , { "assertTitle", "exact:assertion test", Success.class }
            , { "assertTitle", "as*s", Failure.class }
            , { "assertTitle", "glob:as*s", Failure.class }
            , { "assertTitle", "regexp:as.+s$", Failure.class }
            , { "assertTitle", "regexpi:AS.+S$", Failure.class }
            , { "assertTitle", "exact:assertion", Failure.class }
            , { "assertNotTitle", "as*", Failure.class }
            , { "assertNotTitle", "glob:as*", Failure.class }
            , { "assertNotTitle", "regexp:as.+", Failure.class }
            , { "assertNotTitle", "regexpi:AS.+", Failure.class }
            , { "assertNotTitle", "exact:assertion test", Failure.class }
            // for issue 56
            // FIXME: The following command does not work on "selenium-2.32.0".
            // , { "assertText", "test", "something*" }, "assert", "getText", false, false, Success.class }
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
        Runner runner = new Runner();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        wdm.setDriverOptions(new DriverOptions());
        runner.setDriver(wdm.get());
        TestCase testCase = Binder.newTestCase("", "", runner, wsr.getBaseURL());
        CommandFactory commandFactory = runner.getCommandFactory();
        commandFactory.setProc(testCase.getProc());
        testCase.addCommand(commandFactory.newCommand(0, "open", "/assertion.html"));
        testCase.addCommand(commandFactory.newCommand(1, commandName, argument));
        Result result = testCase.execute(null, runner);
        assertThat(result, is(instanceOf(resultClass)));
    }

}
