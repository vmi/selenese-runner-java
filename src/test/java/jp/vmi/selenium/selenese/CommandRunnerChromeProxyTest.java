package jp.vmi.selenium.selenese;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

/**
 * Test for Chrome with proxy.
 */
public class CommandRunnerChromeProxyTest extends CommandRunnerChromeTest {
    static WebrickServer proxy = new Proxy();

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

    @Override
    protected void setupWebDriverManager() {
        driverOptions.set(DriverOption.PROXY, proxy.getServerNameString());
        super.setupWebDriverManager();
    }
}
