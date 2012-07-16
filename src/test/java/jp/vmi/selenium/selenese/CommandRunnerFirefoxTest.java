package jp.vmi.selenium.selenese;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerFirefoxTest extends CommandRunnerTest {
    @Override
    protected WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException {
        return WebDriverFactory.getFactory(FirefoxDriverFactory.class, new DriverOptions());
    }
}
