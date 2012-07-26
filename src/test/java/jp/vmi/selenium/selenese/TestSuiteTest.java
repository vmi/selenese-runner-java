package jp.vmi.selenium.selenese;

import java.io.File;

import org.junit.Test;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class TestSuiteTest {

    @Test
    public void testTestSuite() {
        File script = TestUtils.getScriptFile(TestSuiteTest.class, "");
        Runner runner = new Runner(WebDriverFactory.getFactory(FirefoxDriverFactory.class, new DriverOptions()));
        runner.run(script);

    }
}
