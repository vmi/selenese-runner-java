package jp.vmi.selenium.selenese;

import org.junit.Test;

import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Ieeus #76 Test.
 */
public class Issue76Test {

    /**
     * Popup with highlight test.
     *
     * @throws Exception exception.
     */
    @Test
    public void testPopupWithHighlight() throws Exception {
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
