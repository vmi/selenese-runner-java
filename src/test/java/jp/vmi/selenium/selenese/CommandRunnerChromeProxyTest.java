package jp.vmi.selenium.selenese;

import org.junit.Before;

import jp.vmi.selenium.webdriver.ChromeDriverFactory;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerChromeProxyTest extends CommandRunnerChromeTest {
    @Before
    public void checkProxy() {
        TestUtils.checkProxy();
    }

    @Override
    protected WebDriverFactory getWebDriverFactory() throws IllegalArgumentException {
        DriverOptions options = new DriverOptions();
        options.set(DriverOption.PROXY, "localhost:18080");
        return WebDriverFactory.getFactory(ChromeDriverFactory.class, options);
    }
}
