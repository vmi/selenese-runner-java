package jp.vmi.selenium.selenese;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.Verifier;

import jp.vmi.selenium.testutil.WebProxyResource;
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

    /**
     * check firefox
     */
    @Rule
    public AssumptionFirefox assumptionFirefox = new AssumptionFirefox();

    /**
     * verify used proxy in testmethod.
     */
    @Rule
    public Verifier proxyused = new Verifier() {
        @Override
        protected void verify() throws Throwable {
            assertThat(proxy.getProxy().getCount(), is(greaterThan(0)));
        }
    };

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions().set(DriverOption.PROXY, proxy.getProxy().getServerNameString()));
    }
}
