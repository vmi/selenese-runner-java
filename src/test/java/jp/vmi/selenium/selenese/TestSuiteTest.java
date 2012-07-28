package jp.vmi.selenium.selenese;

import java.io.File;

import org.junit.Test;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class TestSuiteTest {

    @Test
    public void testTestSuite() {
        File script = TestUtils.getScriptFile(TestSuiteTest.class, "");
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
        Runner runner = new Runner(manager.get());
        runner.run(script);
    }
}
