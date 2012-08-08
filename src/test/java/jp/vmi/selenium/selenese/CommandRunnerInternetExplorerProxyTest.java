package jp.vmi.selenium.selenese;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

@Ignore("not yet ready to IE proxy test.")
public class CommandRunnerInternetExplorerProxyTest extends CommandRunnerInternetExplorerTest {
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
        manager.setWebDriverFactory(WebDriverManager.IE);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, proxy.getProxyString()));
    }
}
