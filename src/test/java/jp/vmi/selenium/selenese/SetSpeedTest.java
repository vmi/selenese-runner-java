package jp.vmi.selenium.selenese;

import org.junit.Test;

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
        long startTime = System.currentTimeMillis();
        runner.run(script);
        long endTime = System.currentTimeMillis();
        assertThat((Long) (endTime - startTime), greaterThanOrEqualTo(3 * 1000L));
    }
}
