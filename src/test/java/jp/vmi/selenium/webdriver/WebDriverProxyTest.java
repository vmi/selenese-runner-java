package jp.vmi.selenium.webdriver;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.testutils.WebProxyResource;
import jp.vmi.selenium.testutils.WebServerResouce;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * WebDriver with proxy test.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class WebDriverProxyTest {

    private static final Logger log = LoggerFactory.getLogger(WebDriverProxyTest.class);

    @Parameters(name = "{index}: {0}")
    public static List<Object[]> getWebDriverFactories() {
        return TestUtils.getWebDriverFactories();
    }

    @Rule
    public final WebServerResouce wsr = new WebServerResouce();

    @Rule
    public final WebProxyResource wpr = new WebProxyResource();

    @Parameter
    public String factoryName;

    @Test
    public void testProxy() {
        WebDriverManager manager = WebDriverManager.getInstance();
        WebDriverFactory factory = manager.lookupWebDriverFactory(factoryName);
        assumeTrue("Proxy is not supported.", factory.isProxySupported());
        manager.setWebDriverFactory(factory);
        DriverOptions options = new DriverOptions();
        options.set(DriverOption.PROXY, wpr.getServerNameString());
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
        wpr.resetCount();
        driver.get(wsr.getBaseURL());
        String actualTitle = driver.getTitle();
        int actualCount = wpr.getCount();
        log.info("Title: [{}] / Count: {} ({})", actualTitle, actualCount, factoryName);
        assertThat(actualTitle, is("Index for Unit Test"));
        assumeThat("proxy option does not work on PhantomJS 1.9.2 for Mac OS X",
            driver.getClass().getSimpleName() + "/" + actualCount,
            is(not("PhantomJSDriver/0")));
        assertThat(actualCount, greaterThanOrEqualTo(1));
    }
}
