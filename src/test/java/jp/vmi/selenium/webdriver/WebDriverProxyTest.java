package jp.vmi.selenium.webdriver;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.testutils.WebProxyResource;
import jp.vmi.selenium.testutils.WebServerResouce;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * WebDriver with proxy test.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class WebDriverProxyTest {

    @Parameters
    public static List<Object[]> getWebDriverFactories() {
        return TestUtils.getWebDriverFactories();
    }

    @Rule
    public final WebServerResouce wsr = new WebServerResouce();

    @Rule
    public final WebProxyResource wpr = new WebProxyResource();

    @Parameter
    public WebDriverFactory factory;

    @Test
    public void testProxy() {
        assumeTrue("Proxy is not supported.", factory.isProxySupported());
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(factory);
        DriverOptions options = new DriverOptions();
        options.set(DriverOption.PROXY, wpr.getProxy().getServerNameString());
        manager.setDriverOptions(options);
        WebDriver driver;
        try {
            driver = manager.get();
        } catch (IllegalStateException e) {
            assumeNoException("Initialization failed.", e);
            return;
        } catch (UnsupportedOperationException e) {
            assumeNoException("Unsupported platform.", e);
            return;
        }
        driver.get(wsr.getServer().getBaseURL());
        assertThat(wpr.getProxy().getCount(), is(1));
    }
}
