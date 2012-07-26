package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerFirefoxProxyTest extends CommandRunnerFirefoxTest {
    Proxy proxy = new Proxy();

    @Before
    public void startProxy() {
        proxy.start();
    }

    @After
    public void stopProxy() {
        proxy.stop();
        WebDriverFactory.initFactories();
    }

    @Override
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
        DriverOptions options = new DriverOptions();
        options.set(DriverOption.PROXY, "localhost:" + proxy.getPort());
        return WebDriverFactory.getFactory(FirefoxDriverFactory.class, options);
    }
}
