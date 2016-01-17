package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
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
import org.openqa.selenium.remote.UnreachableBrowserException;

import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.CookieFilter.FilterType;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.testutils.TestCaseTestBase;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.webdriver.DriverOptions;

import static jp.vmi.selenium.webdriver.WebDriverManager.*;
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
    public String currentFactoryName;

    protected final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    @Override
    protected void initDriver() {
        setWebDriverFactory(currentFactoryName, new DriverOptions());
        try {
            driver = manager.get();
        } catch (UnreachableBrowserException e) {
            Assume.assumeNoException(e);
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        }
    }

    public void assumeNot(String... factoryNames) {
        for (String factoryName : factoryNames)
            assumeThat(currentFactoryName, is(not(factoryName)));
    }

    @Test
    public void testSimple() {
        assumeNot(HTMLUNIT); // don't work this test on HtmlUnitDriver.
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
        assumeNot(HTMLUNIT); // don't work this test on HtmlUnitDriver.
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
        runner.setIgnoredScreenshotCommand(true);
        execute("capture");
        assertThat(result, is(instanceOf(Success.class)));
        assertThat(FileUtils.listFiles(screenshotDir.getRoot(), new String[] { "png" }, true), is(empty()));
    }

    @Ignore
    @Test
    public void basicAuth() {
        assumeNot(HTMLUNIT, IE);
        execute("basicAuth");
        runner.setOverridingBaseURL("http://user:pass@" + wsr.getServerNameString() + "/");
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void highlight() {
        assumeNot(HTMLUNIT); // don't work this test on HtmlUnitDriver.
        runner.setHighlight(true);
        runner.setOverridingBaseURL(wsr.getBaseURL());
        execute("highlight");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void locator() {
        // don't work this test-case on SafariDriver and HtmlUnitDriver.
        assumeNot(HTMLUNIT, SAFARI);
        execute("locator");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void iframe() {
        // don't work this test-case on SafariDriver and HtmlUnitDriver.
        assumeNot(HTMLUNIT, SAFARI);
        execute("iframe");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void sendkeys() {
        assumeNot(HTMLUNIT, FIREFOX);
        execute("sendkeys");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void clickableImage() {
        assumeNot(HTMLUNIT);
        execute("clickable_image_test");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void rollup() {
        assumeNot(HTMLUNIT);
        runner.getRollupRules().load(getClass().getResourceAsStream("/rollup/user-extention-rollup.js"));
        execute("rollup");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void cookie() {
        execute("cookie");
        assertThat(result, is(instanceOf(Warning.class)));
        List<String> actual = getSystemOut(new Filter() {
            private boolean fetch = false;

            @Override
            public String filter(String line) {
                if (!fetch && line.contains("deleteAllVisibleCookies")) {
                    fetch = true;
                } else if (fetch && line.contains("- Cookie:")) {
                    return line
                        .replaceFirst("^\\[[^\\]]+\\]\\s+", "") // suppress timestamp.
                        .replaceFirst("domain=\\*", "domain=localhost"); // fixup safari driver's bug.
                }
                return null;
            }
        });
        assertThat(actual, is(equalTo(Arrays.asList(
            "[INFO] - Cookie: [add] key1=[value1] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [add] key2=[value2] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [add] key3=[value3] (domain=localhost, path=/, expire=*)",
            "[ERROR] - Cookie: key1=[value1] (domain=localhost, path=/, expire=*)",
            "[ERROR] - Cookie: key2=[value2] (domain=localhost, path=/, expire=*)",
            "[ERROR] - Cookie: key3=[value3] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [mod] key2=[VALUE_TWO] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [del] key3 (domain=localhost, path=/)"))));

        testSuites.clear();
        execute("cookie2");
        assertThat(result, is(instanceOf(Success.class)));
        List<String> actual2 = getSystemOut(new Filter() {

            @Override
            public String filter(String line) {
                if (line.contains("- Cookie:")) {
                    return line
                        .replaceFirst("^\\[[^\\]]+\\]\\s+", "") // suppress timestamp.
                        .replaceFirst("domain=\\*", "domain=localhost"); // fixup safari driver's bug.
                }
                return null;
            }
        });
        assertThat(actual2, is(equalTo(Arrays.asList(
            "[INFO] - Cookie: key1=[value1] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: key2=[VALUE_TWO] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [del] key1 (domain=localhost, path=/)",
            "[INFO] - Cookie: [del] key2 (domain=localhost, path=/)"))));
    }

    @Test
    public void cookieFilter() {
        runner.setCookieFilter(new CookieFilter(FilterType.SKIP, "key2"));
        execute("cookie");
        assertThat(result, is(instanceOf(Warning.class)));
        List<String> actual = getSystemOut(new Filter() {
            private boolean fetch = false;

            @Override
            public String filter(String line) {
                if (!fetch && line.contains("deleteAllVisibleCookies")) {
                    fetch = true;
                } else if (fetch && line.contains("- Cookie:")) {
                    return line
                        .replaceFirst("^\\[[^\\]]+\\]\\s+", "") // suppress timestamp.
                        .replaceFirst("domain=\\*", "domain=localhost"); // fixup safari driver's bug.
                }
                return null;
            }
        });
        assertThat(actual, is(equalTo(Arrays.asList(
            "[INFO] - Cookie: [add] key1=[value1] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [add] key3=[value3] (domain=localhost, path=/, expire=*)",
            "[ERROR] - Cookie: key1=[value1] (domain=localhost, path=/, expire=*)",
            "[ERROR] - Cookie: key3=[value3] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [del] key3 (domain=localhost, path=/)"))));

        testSuites.clear();
        runner.setCookieFilter(new CookieFilter(FilterType.PASS, "key1"));
        execute("cookie2");
        assertThat(result, is(instanceOf(Success.class)));
        List<String> actual2 = getSystemOut(new Filter() {

            @Override
            public String filter(String line) {
                if (line.contains("- Cookie:")) {
                    return line
                        .replaceFirst("^\\[[^\\]]+\\]\\s+", "") // suppress timestamp.
                        .replaceFirst("domain=\\*", "domain=localhost"); // fixup safari driver's bug.
                }
                return null;
            }
        });
        assertThat(actual2, is(equalTo(Arrays.asList(
            "[INFO] - Cookie: key1=[value1] (domain=localhost, path=/, expire=*)",
            "[INFO] - Cookie: [del] key1 (domain=localhost, path=/)"))));
    }

    @Test
    public void issue48() {
        assumeNot(SAFARI); // FIXME don't work this test on SafariDriver.
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
        assumeNot(HTMLUNIT); // don't work this test on HtmlUnitDriver.
        execute("issue55");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue76() {
        assumeNot(SAFARI); // FIXME don't work this test on SafariDriver.
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

    @Test
    public void issue99() {
        assumeNot(HTMLUNIT, PHANTOMJS);
        execute("issue99");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue114() {
        execute("issue114_2");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void mouseEvent() {
        assumeNot(HTMLUNIT);
        execute("testsuite_mouse");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue179() {
        assumeNot(HTMLUNIT);
        execute("testcase_issue179");
        assertThat(result, is(instanceOf(Success.class)));
    }
}
