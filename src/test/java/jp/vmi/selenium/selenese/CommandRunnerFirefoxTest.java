package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;

import jp.vmi.selenium.webdriver.BrowserNotFoundException;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    @Before
    public void assumeInstalledFirefox() throws IllegalArgumentException {
        try {
            FirefoxDriverFactory f = new FirefoxDriverFactory(new DriverOptions());
            f.initDriver();
        } catch (BrowserNotFoundException e) {
            Assume.assumeNoException(e);
        }
    }

    @Override
    protected WebDriverFactory getWebDriverFactory() throws IllegalArgumentException {
        return WebDriverFactory.getFactory(FirefoxDriverFactory.class, new DriverOptions());
    }
}
