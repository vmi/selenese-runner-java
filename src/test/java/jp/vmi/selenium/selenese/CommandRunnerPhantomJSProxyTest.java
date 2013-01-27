package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.junit.Assert.*;

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

    @After
    public void checkCount() {
        assertTrue(proxy.getCount() > 0);
    }

    @Override
    protected void setupWebDriverManager() {
        super.setupWebDriverManager();
        WebDriverManager manager = WebDriverManager.getInstance();
        DriverOptions opts = manager.getDriverOptions();
        opts.set(DriverOption.PROXY, proxy.getServerNameString());
    }

}
