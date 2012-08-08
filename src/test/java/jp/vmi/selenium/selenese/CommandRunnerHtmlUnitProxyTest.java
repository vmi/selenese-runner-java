package jp.vmi.selenium.selenese;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandRunnerHtmlUnitProxyTest extends CommandRunnerHtmlUnitTest {
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
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, proxy.getProxyString()));
    }
}
