package jp.vmi.selenium.selenese.utils;

import org.junit.Test;

import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.DriverDependentTestCaseTestBase;

import static jp.vmi.selenium.webdriver.WebDriverManager.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class WindowSelectorTest extends DriverDependentTestCaseTestBase {

    @Test
    public void testSelectWindow() {
        // FIXME Don't work with FirefoxDriver of Selenium 3.3.1.
        assumeNot(FIREFOX);
        execute("testcase_issue199");
        assertThat(result, is(instanceOf(Success.class)));
    }
}
