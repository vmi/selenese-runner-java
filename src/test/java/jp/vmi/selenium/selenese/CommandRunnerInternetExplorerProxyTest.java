package jp.vmi.selenium.selenese;

import org.junit.Before;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.IEDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerInternetExplorerProxyTest extends CommandRunnerInternetExplorerTest {
    @Before
    public void checkProxy() {
        TestUtils.checkProxy();
    }

    @Override
    protected WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException {
        DriverOptions options = new DriverOptions();
        options.set(DriverOption.PROXY, "localhost:18080");
        return WebDriverFactory.getFactory(IEDriverFactory.class, options);
    }
}
