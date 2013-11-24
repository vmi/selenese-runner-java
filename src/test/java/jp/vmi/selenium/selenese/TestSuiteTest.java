package jp.vmi.selenium.selenese;

import org.junit.Test;

import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for {@link TestSuite}.
 */
public class TestSuiteTest extends TestBase {

    /**
     * Test of "TestSuiteTest.html".
     */
    @Test
    public void testTestSuite() {
        String script = TestUtils.getScriptFile("testSuite");
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        manager.setDriverOptions(new DriverOptions());
        Runner runner = new Runner();
        runner.setDriver(manager.get());
        runner.setBaseURL(wsr.getBaseURL());
        runner.run(script);
    }
}
