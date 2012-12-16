package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for issue #49.
 */
public class Issue50Test extends TestBase {

    private final Runner runner = new Runner();

    private static boolean noDisplay = false;

    /**
     * Initialize Firefox.
     */
    @Before
    public void initialize() {
        if (noDisplay)
            throw new AssumptionViolatedException("no display specified");
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        } catch (WebDriverException e) {
            Assume.assumeNoException(e);
        }
        try {
            WebDriverManager manager = WebDriverManager.getInstance();
            manager.setWebDriverFactory(WebDriverManager.FIREFOX);
            runner.setDriver(manager.get());
        } catch (WebDriverException e) {
            if (e.getMessage().contains("no display specified")) {
                noDisplay = true;
                Assume.assumeNoException(e);
            }
        }
    }

    /**
     * Test about issue #50.
     */
    @Test
    public void test() {
        String html = TestUtils.getScriptFile(getClass());
        Result result = runner.run(html);
        assertThat(result, is(instanceOf(Success.class)));
    }
}
