package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.Verifier;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test for Firefox with proxy.
 */
public class CommandRunnerFirefoxProxyTest extends CommandRunnerFirefoxTest {
    /**
     * proxy resource
     */
    @ClassRule
    public static WebProxyResource proxy = new WebProxyResource();

    @Rule
    public AssumptionFirefox assumptionFirefox = new AssumptionFirefox();

    /**
     * verify used proxy in testmethod.
     */
    @ClassRule
    public static Verifier proxyused = new Verifier() {
        @Override
        protected void verify() throws Throwable {
            assertThat(proxy.getProxy().getCount(), is(greaterThan(0)));
        }
    };

    @Override
    @Before
    public void assumeInstalledFirefox() {
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        } catch (WebDriverException e) {
            Assume.assumeNoException(e);
        }
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, proxy.getProxy().getServerNameString()));
    }
}
