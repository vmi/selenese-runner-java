package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for PhantomJS.
 */
public class CommandRunnerPhantomJSTest extends CommandRunnerTest {

    /**
     * Check Firefox installation.
     */
    @Before
    public void assumeInstalledPhantomJS() {
        try {
            new PhantomJSDriver(DesiredCapabilities.phantomjs());
        } catch (IllegalStateException e) {
            Assume.assumeNoException(e);
        }
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.PHANTOMJS);
        manager.setDriverOptions(new DriverOptions());
    }
}
