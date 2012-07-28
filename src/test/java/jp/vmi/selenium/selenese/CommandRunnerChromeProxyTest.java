package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.Before;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandRunnerChromeProxyTest extends CommandRunnerChromeTest {
    Proxy proxy = new Proxy();

    @Before
    public void startProxy() {
        proxy.start();
    }

    @After
    public void stopProxy() {
        proxy.stop();
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory("chrome");
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, "localhost:18080"));
    }
}
