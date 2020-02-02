package jp.vmi.selenium.selenese.utils;

import org.junit.Test;

import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.DriverDependentTestCaseTestBase;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("javadoc")
public class WindowSelectorTest extends DriverDependentTestCaseTestBase {

    @Test
    public void testSelectWindow() {
        execute("testcase_issue199");
        assertThat(result, is(instanceOf(Success.class)));
    }
}
