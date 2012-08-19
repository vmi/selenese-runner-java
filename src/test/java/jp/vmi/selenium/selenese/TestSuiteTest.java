package jp.vmi.selenium.selenese;

import java.io.File;

import org.junit.Test;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for {@link TestSuite}.
 */
public class TestSuiteTest {

    /**
     * Test of "TestSuiteTest.html".
     */
    @Test
    public void testTestSuite() {
        File script = TestUtils.getScriptFile(TestSuiteTest.class, "");
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        manager.setDriverOptions(new DriverOptions());
        Runner runner = new Runner();
        runner.setDriver(manager.get());
        runner.run(script);
    }
}
