package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test for Firefox with proxy.
 */
public class CommandRunnerFirefoxProxyTest extends CommandRunnerFirefoxTest {
    static Proxy proxy = new Proxy();

    /**
     * Start proxy server.
     */
    @BeforeClass
    public static void startProxy() {
        proxy.start();
    }

    /**
     * Stop proxy server.
     */
    @AfterClass
    public static void stopProxy() {
        proxy.kill();
    }

    /**
     * test using proxy
     */
    @After
    public void checkCount() {
        assertThat(proxy.getCount(), is(greaterThan(0)));
    }

    @Override
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

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, proxy.getServerNameString()));
    }
}
