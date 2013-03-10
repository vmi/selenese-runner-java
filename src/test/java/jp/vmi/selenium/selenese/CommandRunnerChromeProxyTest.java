package jp.vmi.selenium.selenese;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.Verifier;

import jp.vmi.selenium.testutil.WebProxyResource;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test for Chrome with proxy.
 */
public class CommandRunnerChromeProxyTest extends CommandRunnerChromeTest {
    /**
     * proxy resource
     */
    @ClassRule
    public static WebProxyResource proxy = new WebProxyResource();

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
        driverOptions.set(DriverOption.PROXY, proxy.getProxy().getServerNameString());
        super.setupWebDriverManager();
    }
}
