package jp.vmi.selenium.selenese;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestCaseTestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Driver independent test.
 */
@SuppressWarnings("javadoc")
public class DriverIndependentTest extends TestCaseTestBase {

    @Override
    protected void initDriver() {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        driver = manager.get();
    }

    @Test
    public void testFlowControl() throws IllegalArgumentException {
        execute("flowControl");
        assertThat(xmlResult, containsString("[Finished with x = 15 and y = 14]"));
    }

    @Test
    public void testForEach() throws IllegalArgumentException {
        execute("forEach");
        assertThat(xmlResult, containsString("[chedder]"));
        assertThat(xmlResult, containsString("[edam]"));
        assertThat(xmlResult, containsString("[swiss]"));
    }

    @Test
    public void noCommandSelenese() throws IllegalArgumentException {
        execute("noCommand");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void verifyNotText() throws IllegalArgumentException {
        execute("verifyNotText");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void invalidCommandInHtml() throws IllegalArgumentException {
        execute("invalidCommand");
        assertThat(result, is(instanceOf(Error.class)));
        assertThat(result.getMessage(), containsString("No such command"));
    }

    @Test
    public void pauseCommand() throws IllegalArgumentException {
        StopWatch sw = new StopWatch();
        sw.start();
        execute("pause");
        sw.stop();
        assertThat(result, is(instanceOf(Success.class)));
        assertThat(sw.getTime(), is(greaterThanOrEqualTo(2900L)));
    }

    @Test
    public void setSpeed() throws IllegalArgumentException {
        StopWatch sw = new StopWatch();
        sw.start();
        execute("setSpeed");
        sw.stop();
        assertThat(result, is(instanceOf(Success.class)));
        assertThat(sw.getTime(), is(greaterThanOrEqualTo(2900L)));
    }

    @Test
    public void setTimeout() throws IllegalArgumentException {
        execute("setTimeout");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void emptyFile() throws IOException {
        execute("empty");
        assertThat(result, is(instanceOf(Error.class)));
        assertThat(result.getMessage(), containsString("Not selenese script."));
    }

    /**
     * Return error result if test-case is not found in test-suite.
     * (Don't throw Exception)
     *
     * @throws FileNotFoundException
     */
    @Test
    public void issue75() throws FileNotFoundException {
        execute("issue75");
        assertThat(result, is(instanceOf(Error.class)));
        assertThat(result.getMessage(), containsString("notFound.html"));
    }

    @Test
    public void javascriptBlock() {
        Runner runner = new Runner();
        runner.setDriver(driver);
        runner.setOverridingBaseURL(wsr.getBaseURL());
        CommandFactory cf = runner.getCommandFactory();
        TestCase testCase = Binder.newTestCase("dummy", "dummy", wsr.getBaseURL());
        testCase.addCommand(cf, "open", "/index.html");
        testCase.addCommand(cf, "store", "javascript{'x'}", "a");
        runner.execute(testCase);
        assertThat(runner.getVarsMap().get("a").toString(), equalTo("x"));
    }

    @Test
    public void issue163() {
        execute("issue163");
        assertThat(result, is(instanceOf(Success.class)));
    }
}
