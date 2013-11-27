package jp.vmi.selenium.selenese;

import org.junit.Test;

import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Ieeus #82 Test.
 */
public class Issue82Test {

    /**
     * Iframe test.
     *
     * @throws Exception exception.
     */
    @Test
    public void testIframe() throws Exception {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        Runner runner = new Runner();
        runner.setHighlight(true);
        runner.setDriver(manager.get());
        String html = TestUtils.getScriptFile(getClass());
        Result result = runner.run(html);
        assertThat(result, is(instanceOf(Success.class)));
    }
}
