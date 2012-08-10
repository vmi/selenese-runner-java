package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for Firefox.
 */
public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    /**
     * Check Firefox installation.
     */
    @Before
    public void assumeInstalledFirefox() {
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        }
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
    }
}
