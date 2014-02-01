package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.safari.SafariDriver;

import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestCaseTestBase;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * Driver dependent test.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class DriverDependentTest extends TestCaseTestBase {

    @Parameters(name = "{index}: {0}")
    public static List<Object[]> getWebDriverFactories() {
        return TestUtils.getWebDriverFactories();
    }

    @Parameter
    public String factoryName;

    protected final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    @Override
    protected void initDriver() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(factoryName);
        manager.setDriverOptions(new DriverOptions());
        try {
            driver = manager.get();
        } catch (UnreachableBrowserException e) {
            Assume.assumeNoException(e);
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        }
    }

    public void assumeNot(Class<? extends WebDriver> driverClass) {
        assumeThat(driver, is(not(instanceOf(driverClass))));
    }

    @Test
    public void testSimple() {
        assumeNot(HtmlUnitDriver.class); // don't work this test on HtmlUnitDriver.
        execute("simple");
        assertThat(result, is(instanceOf(Success.class)));
        assertThat(xmlResult, containsString("Command#4"));
    }

    @Test
    public void testLocatorFail() {
        execute("error");
        assertThat(result, is(instanceOf(Failure.class)));
        assertThat(result.getMessage(), containsString("Element name=no_such_name not found"));
    }

    @Test
    public void testAssertFail() {
        assumeNot(HtmlUnitDriver.class); // don't work this test on HtmlUnitDriver.
        execute("assertFail");
        assertThat(result, is(instanceOf(Failure.class)));
        assertThat(result.getMessage(), containsString("Result: [selenium] / Expected: [no such text]"));
    }

    @Test
    public void capture() {
        assumeThat(driver, is(instanceOf(TakesScreenshot.class)));
        File pngFile = new File(screenshotDir.getRoot(), "test.png");
        if (pngFile.exists())
            pngFile.delete();
        execute("capture");
        assertThat(result, is(instanceOf(Success.class)));
        assertThat("Captured File: " + pngFile, pngFile.exists(), is(true));
    }

    @Test
    public void ignoreScreenshotCommand() {
        assumeThat(driver, is(instanceOf(TakesScreenshot.class)));
        runner.setIgnoreScreenshotCommand(true);
        execute("capture");
        assertThat(result, is(instanceOf(Success.class)));
        assertThat(FileUtils.listFiles(screenshotDir.getRoot(), new String[] { "png" }, true), is(empty()));
    }

    @Ignore
    @Test
    public void basicAuth() {
        assumeNot(InternetExplorerDriver.class);
        assumeNot(HtmlUnitDriver.class);

        execute("basicAuth");
        runner.setBaseURL("http://user:pass@" + wsr.getServerNameString() + "/");
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void highlight() {
        assumeNot(HtmlUnitDriver.class); // don't work this test on HtmlUnitDriver.
        runner.setHighlight(true);
        runner.setBaseURL(wsr.getBaseURL());
        execute("highlight");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void locator() {
        // don't work this test-case on SafariDriver and HtmlUnitDriver.
        assumeNot(HtmlUnitDriver.class);
        assumeNot(SafariDriver.class);
        execute("locator");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void iframe() {
        // don't work this test-case on SafariDriver and HtmlUnitDriver.
        assumeNot(HtmlUnitDriver.class);
        assumeNot(SafariDriver.class);
        execute("iframe");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void test() {
        assumeNot(HtmlUnitDriver.class);
        assumeNot(FirefoxDriver.class);
        execute("sendkeys");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue48() {
        assumeNot(SafariDriver.class); // FIXME don't work this test on SafariDriver.
        execute("issue48");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue49_50() {
        execute("issue49+50");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue55() {
        assumeNot(HtmlUnitDriver.class); // don't work this test on HtmlUnitDriver.
        execute("issue55");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue76() {
        assumeNot(SafariDriver.class); // FIXME don't work this test on SafariDriver.
        execute("issue76");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue77() {
        execute("issue77");
        assertThat(result, is(instanceOf(Failure.class)));
        assertThat(result.getMessage(), containsString("Element css=select[name='not_found'] not found"));
    }

    @Test
    public void issue87() {
        execute("verifyCssCount");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue93() {
        execute("issue93");
        assertThat(result, is(instanceOf(Success.class)));
    }
}
