package jp.vmi.selenium.selenese;

import org.junit.Rule;
import org.junit.Test;

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

    /**
     * assumption firefox.
     */
    @Rule
    public AssumptionFirefox nofirefox = new AssumptionFirefox();

    /**
     * Test about issue #50.
     */
    @Test
    public void test() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        runner.setDriver(manager.get());

        String html = TestUtils.getScriptFile(getClass());
        Result result = runner.run(html);
        assertThat(result, is(instanceOf(Success.class)));
    }
}
