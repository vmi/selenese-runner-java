package jp.vmi.selenium.selenese;

import org.junit.Test;

import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestBaseWithMultiDriver;
import jp.vmi.selenium.testutils.TestUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Ieeus #76 Test.
 */
public class Issue76Test extends TestBaseWithMultiDriver {

    /**
     * Popup with highlight test.
     *
     * @throws Exception exception.
     */
    @Test
    public void testPopupWithHighlight() throws Exception {
        runner.setHighlight(true);
        String html = TestUtils.getScriptFile(getClass());
        Result result = runner.run(html);
        assertThat(result, is(instanceOf(Success.class)));
    }
}
