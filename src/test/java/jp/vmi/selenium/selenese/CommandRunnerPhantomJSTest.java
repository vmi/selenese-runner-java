package jp.vmi.selenium.selenese;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for PhantomJS.
 */
public class CommandRunnerPhantomJSTest extends CommandRunnerTest {

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.PHANTOMJS);
        manager.setDriverOptions(new DriverOptions());
    }
}
