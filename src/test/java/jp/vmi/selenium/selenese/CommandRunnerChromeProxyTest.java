package jp.vmi.selenium.selenese;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

public class CommandRunnerChromeProxyTest extends CommandRunnerChromeTest {
    static Proxy proxy = new Proxy();

    @BeforeClass
    public static void startProxy() {
        proxy.start();
    }

    @AfterClass
    public static void stopProxy() {
        proxy.kill();
    }

    @Override
    protected void setupWebDriverManager() {
        driverOptions.set(DriverOption.PROXY, proxy.getProxyString());
        super.setupWebDriverManager();
    }
}
