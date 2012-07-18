package jp.vmi.selenium.selenese;

import jp.vmi.selenium.webdriver.BrowserNotFoundException;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

import org.junit.Assume;

public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    @Override
    protected WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException {
        try {
            return WebDriverFactory.getFactory(FirefoxDriverFactory.class, new DriverOptions());
        } catch (BrowserNotFoundException e) {
            Assume.assumeNoException(e);
            return null; // not reached;
        }
    }
}
