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
    private final String locator;
    private final String pattern;
    private final Class<? extends Result> resultClass;

    /**
     * Construct testcase by parameters.
     *
     * @param commandName assert command name
     * @param locator locator
     * @param pattern matching pattern
     * @param resultClass expected result class
     */
    public AssertionStringMatchTest(String commandName, String locator, String pattern, Class<? extends Result> resultClass) {
        this.commandName = commandName;
        this.locator = locator;
        this.pattern = pattern;
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
            { "assertText", "class=text1", "as*", Success.class },
            { "assertText", "class=text1", "glob:as*", Success.class },
            { "assertText", "class=text1", "regexp:as.+", Success.class },
            { "assertText", "class=text1", "regexpi:AS.+", Success.class },
            { "assertText", "class=text1", "exact:assertion test", Success.class },
            { "assertText", "class=text1", "as*s", Failure.class },
            { "assertText", "class=text1", "glob:as*s", Failure.class },
            { "assertText", "class=text1", "regexp:as.+s$", Failure.class },
            { "assertText", "class=text1", "regexpi:AS.+S$", Failure.class },
            { "assertText", "class=text1", "exact:assertion", Failure.class },
            { "assertNotText", "class=text1", "as*", Failure.class },
            { "assertNotText", "class=text1", "glob:as*", Failure.class },
            { "assertNotText", "class=text1", "regexp:as.+", Failure.class },
            { "assertNotText", "class=text1", "regexpi:AS.+", Failure.class },
            { "assertNotText", "class=text1", "exact:assertion test", Failure.class }
            // for issue 56
            // FIXME: The following command does not work on "selenium-2.32.0".
            // , { "assertText", "test", "something*" }, "assert", "getText", false, false, Success.class }
        });
    }

    /**
     * test for string-match (old style)
     *
     * @see "http://release.seleniumhq.org/selenium-core/1.0.1/reference.html#patterns"
     *
     * @throws IOException exception.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void stringMatchPatternOld() throws IOException {
        Runner runner = new Runner();
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        runner.setDriver(manager.get());
        TestCase testCase = Binder.newTestCase("dummy", "dummy", runner, wsr.getBaseURL());
        CommandFactory commandFactory = runner.getCommandFactory();
        commandFactory.setProc(testCase.getProc());
        testCase.addCommand(commandFactory.newCommand(0, "open", "/assertion.html"));
        testCase.addCommand(commandFactory.newCommand(1, commandName, locator, pattern));
        Result result = testCase.execute(null, runner);
        assertThat(result, is(instanceOf(resultClass)));
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
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        Runner runner = new Runner();
        runner.setDriver(manager.get());
        CommandFactory cf = runner.getCommandFactory();
        TestCase testCase = Binder.newTestCase("dummy", "dummy", wsr.getBaseURL());
        testCase.addCommand(cf, "open", "/assertion.html");
        testCase.addCommand(cf, commandName, locator, pattern);
        Result result = runner.execute(testCase);
        assertThat(result, is(instanceOf(resultClass)));
    }
}
