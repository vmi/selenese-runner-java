package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriverService;

import jp.vmi.selenium.webdriver.ChromeDriverFactory;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerChromeTest extends CommandRunnerTest {
    @Override
    protected WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException {
        return WebDriverFactory.getFactory(ChromeDriverFactory.class, new DriverOptions());
    }

    @Before
    public void checkChromeProperty() {
        Assume.assumeNotNull(System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY));
    }
}
