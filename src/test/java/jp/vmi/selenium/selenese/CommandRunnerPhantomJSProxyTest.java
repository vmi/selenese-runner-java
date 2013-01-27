package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test for PhantomJS with proxy.
 */
public class CommandRunnerPhantomJSProxyTest extends CommandRunnerPhantomJSTest {
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
    protected void setupWebDriverManager() {
        super.setupWebDriverManager();
        WebDriverManager manager = WebDriverManager.getInstance();
        DriverOptions opts = manager.getDriverOptions();
        opts.set(DriverOption.PROXY, proxy.getServerNameString());
    }

}
