package jp.vmi.selenium.selenese;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import jp.vmi.selenium.testutils.TestUtils;

import jp.vmi.selenium.webdriver.WebDriverManager;
import static org.hamcrest.number.OrderingComparison.*;
import static org.junit.Assert.*;

/**
 * command of "setSpeed" test.
 */
public class SetSpeedTest {

    /**
     * Test of "setSpeed".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void test() throws IllegalArgumentException {
        String script = TestUtils.getScriptFile(SetSpeedTest.class, "");
        Runner runner = new Runner();
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        runner.setDriver(manager.get());
        StopWatch sw = new StopWatch();
        sw.start();
        runner.run(script);
        sw.stop();
        assertThat(sw.getTime(), greaterThanOrEqualTo(3 * 1000L));
    }
}
