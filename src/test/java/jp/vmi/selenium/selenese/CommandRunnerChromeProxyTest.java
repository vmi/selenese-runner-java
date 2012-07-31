package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.Before;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

public class CommandRunnerChromeProxyTest extends CommandRunnerChromeTest {
    Proxy proxy = new Proxy();

    @Before
    public void startProxy() {
        proxy.start();
    }

    @After
    public void stopProxy() {
        proxy.kill();
    }

    @Override
    protected void setupWebDriverManager() {
        driverOptions.set(DriverOption.PROXY, "localhost:" + proxy.getPort());
        super.setupWebDriverManager();
    }
}
