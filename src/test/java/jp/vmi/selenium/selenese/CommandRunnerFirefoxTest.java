package jp.vmi.selenium.selenese;

import jp.vmi.selenium.webdriver.BrowserNotFoundException;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

import org.junit.Assume;
import org.junit.Before;

public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    @Before
    public void assumeInstalledFirefox() throws InvalidConfigurationException {
        try {
            FirefoxDriverFactory f = new FirefoxDriverFactory(new DriverOptions());
            f.initDriver();
        } catch (BrowserNotFoundException e) {
            Assume.assumeNoException(e);
        }
    }

    @Override
    protected WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException {
        return WebDriverFactory.getFactory(FirefoxDriverFactory.class, new DriverOptions());
    }
}
