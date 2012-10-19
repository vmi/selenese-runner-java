package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.junit.internal.AssumptionViolatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for Firefox.
 */
public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    private static boolean noDisplay = false;

    /**
     * Check Firefox installation.
     */
    @Before
    public void assumeInstalledFirefox() {
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        } catch (WebDriverException e) {
            Assume.assumeNoException(e);
        }
    }

    /**
     * Check Firefox connected.
     */
    @Before
    public void assumeConnectFirefox() {
        if (noDisplay)
            throw new AssumptionViolatedException("no display specified");

        setupWebDriverManager();
        try {
            WebDriverManager.getInstance().get();
        } catch (WebDriverException e) {
            if (e.getMessage().contains("no display specified")) {
                noDisplay = true;
                Assume.assumeNoException(e);
            }
        }
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
    }
}
