package jp.vmi.selenium.selenese;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

@Ignore("not yet ready to safari proxy test.")
public class CommandRunnerSafariProxyTest extends CommandRunnerSafariTest {
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
        manager.setWebDriverFactory("safari");
        manager.setDriverOptions(new DriverOptions());
    }
}
