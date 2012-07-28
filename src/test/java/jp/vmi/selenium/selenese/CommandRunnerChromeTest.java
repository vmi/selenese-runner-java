package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriverService;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandRunnerChromeTest extends CommandRunnerTest {

    @Before
    public void checkChromeProperty() {
        Assume.assumeNotNull(System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY));
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.CHROME);
        manager.setDriverOptions(new DriverOptions());
    }
}
