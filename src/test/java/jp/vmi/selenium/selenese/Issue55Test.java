package jp.vmi.selenium.selenese;

import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import jp.vmi.selenium.testutils.TestBase;

import jp.vmi.selenium.testutils.AssumptionFirefox;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for issue #55.
 */
public class Issue55Test extends TestBase {

    private final Runner runner = new Runner();

    /**
     * check firefox
     */
    @Rule
    public AssumptionFirefox assumptionFirefox = new AssumptionFirefox();

    /**
     * Test about issue #50.
     */
    @Test
    public void test() {
        String html = TestUtils.getScriptFile(getClass());
        runner.setBaseURL(ws.getUrl());
        runner.setDriver(new FirefoxDriver());
        Result result = runner.run(html);
        assertThat(result, is(instanceOf(Success.class)));
    }
}
