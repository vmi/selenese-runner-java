package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.Before;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.IEDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerInternetExplorerProxyTest extends CommandRunnerInternetExplorerTest {
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
    protected WebDriverFactory getWebDriverFactory() throws IllegalArgumentException {
        DriverOptions options = new DriverOptions();
        options.set(DriverOption.PROXY, "localhost:18080");
        return WebDriverFactory.getFactory(IEDriverFactory.class, options);
    }
}
