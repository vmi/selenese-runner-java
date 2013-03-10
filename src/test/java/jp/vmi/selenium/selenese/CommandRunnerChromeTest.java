package jp.vmi.selenium.selenese;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for Chrome.
 */
public class CommandRunnerChromeTest extends CommandRunnerTest {

    protected DriverOptions driverOptions = new DriverOptions();

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.CHROME);
        manager.setDriverOptions(driverOptions);
    }
}
