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
 * Test for HtmlUnit with proxy.
 */
public class CommandRunnerHtmlUnitProxyTest extends CommandRunnerHtmlUnitTest {
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
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, proxy.getServerNameString()));
    }
}
