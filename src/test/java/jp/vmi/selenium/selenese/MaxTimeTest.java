package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.result.*;
import jp.vmi.selenium.testutils.TestCaseTestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Driver independent test.
 */
@SuppressWarnings("javadoc")
public class MaxTimeTest extends TestCaseTestBase {

    private static final Logger log = LoggerFactory.getLogger(MaxTimeTest.class);

    @Override
    protected void initDriver() {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        driver = manager.get();
    }

    protected void execute(String scriptName, long maxtime) {
        StopWatch sw = new StopWatch();
        runner.setupMaxTimeTimer(maxtime);
        sw.start();
        execute(scriptName);
        sw.stop();
        assertThat(sw.getTime(), is(lessThanOrEqualTo(maxtime + 1000)));
    }

    private CommandResult getLastCommandResult() {
        CommandResultList list = runner.getCurrentTestCase().getResultList();
        return list.get(list.size() - 1);
    }

    @Test
    public void flowControl() throws IllegalArgumentException {
        execute("maxTimeFlowControl", 1000);
        assertThat(result, is(instanceOf(MaxTimeExceeded.class)));
        assertThat(getLastCommandResult().getCommand().getName(), anyOf(is("while"),is("endWhile")));
    }

    @Test
    public void verifyNotText() throws IllegalArgumentException {
        execute("maxTimeVerifyNotText", 1500);
        assertThat(result, is(instanceOf(MaxTimeExceeded.class)));
        assertThat(getLastCommandResult().getCommand().getName(), is("verifyNotText"));
    }

    @Test
    public void pauseCommand() throws IllegalArgumentException {
        execute("pause", 2000);
        assertThat(result, is(instanceOf(MaxTimeExceeded.class)));
        assertThat(getLastCommandResult().getCommand().getName(), is("pause"));
    }

    @Test
    public void setSpeed() throws IllegalArgumentException {
        execute("maxTimeSetSpeed", 2000);
        assertThat(result, is(instanceOf(MaxTimeExceeded.class)));
        assertThat(getLastCommandResult().getCommand().getName(), is("setSpeed"));
    }

    @Test
    public void waitForCondition() {
        execute("maxTimeWaitForCondition", 5000);
        assertThat(result, is(instanceOf(MaxTimeExceeded.class)));
        assertThat(getLastCommandResult().getCommand().getName(), is("waitForCondition"));
    }

    @Test
    public void clickAndWait() {
        execute("maxTimeClickAndWait", 5000);
        assertThat(result, is(instanceOf(MaxTimeExceeded.class)));
        assertThat(getLastCommandResult().getCommand().getName(), is("clickAndWait"));
    }
}
