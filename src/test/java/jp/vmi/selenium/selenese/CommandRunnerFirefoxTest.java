package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    @Before
    public void assumeInstalledFirefox() {
        try {
            new FirefoxDriverFactory(new DriverOptions());
        } catch (IllegalArgumentException e) {
            Assume.assumeNoException(e);
        }
    }

    @Override
    protected WebDriverFactory getWebDriverFactory() throws IllegalArgumentException {
        return WebDriverFactory.getFactory(FirefoxDriverFactory.class, new DriverOptions());
    }
}
