package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

@Ignore("not yet ready to IE proxy test.")
public class CommandRunnerInternetExplorerProxyTest extends CommandRunnerInternetExplorerTest {
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
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.IE);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, "localhost:18080"));
    }
}
