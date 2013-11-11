package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.testutils.TestCaseTestBase;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
    public WebDriverFactory factory;

    protected final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    /**
     * Setup WebDriverManager.
     */
    @Before
    public void initialize() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(factory);
        manager.setDriverOptions(new DriverOptions());
        driver = manager.get();
    }

    /**
     * Test of "CommandRunnerTestSimple.html".
     *
     * @throws IllegalArgumentException exception
     * @throws IOException exception
     */
    @Test
    public void testSimple() throws IllegalArgumentException, IOException {
        execute("simple");
        assertEquals(0, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
        if (runner.getDriver() instanceof TakesScreenshot)
            assertEquals(5, screenshotDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestError.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testFailSubmit() throws IllegalArgumentException {
        execute("error");
        if (runner.getDriver() instanceof TakesScreenshot) {
            assertEquals(1, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
            assertEquals(3, screenshotDir.getRoot().listFiles(pngFilter).length);
        }
    }

    /**
     * Test of "CommandRunnerTestAssertFail.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testAssertFail() throws IllegalArgumentException {
        execute("assertFail");
        if (runner.getDriver() instanceof TakesScreenshot) {
            assertEquals(1, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
            assertEquals(5, screenshotDir.getRoot().listFiles(pngFilter).length);
        }
    }

    /**
     * Test of capture screenshot.
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void capture() throws IllegalArgumentException {
        final String filename = "test.png";
        File pngFile = new File(screenshotDir.getRoot(), filename);
        if (pngFile.exists())
            pngFile.delete();
        initRunner();
        CommandFactory commandFactory = runner.getCommandFactory();
        TestCase testCase = Binder.newTestCase(null, "capture", runner, wsr.getBaseURL());
        commandFactory.setProc(testCase.getProc());
        Command captureCommand = commandFactory.newCommand(1, "captureEntirePageScreenshot", pngFile.getAbsolutePath());
        testCase.addCommand(captureCommand);
        testCase.execute(null, runner);
        if (driver instanceof TakesScreenshot)
            assertTrue(pngFile.exists());
    }

    /**
     * Test of "basic auth access"
     */
    @Ignore
    @Test
    public void basicauth() {
        Assume.assumeThat(WebDriverManager.getInstance().get(), not(instanceOf(InternetExplorerDriver.class)));

        //TODO test fail htmlunit caused by error on getTitle
        Assume.assumeThat(WebDriverManager.getInstance().get(), not(instanceOf(HtmlUnitDriver.class)));

        execute("basicAuth");
        runner.setBaseURL("http://user:pass@" + wsr.getServerNameString() + "/");
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void highlight() {
        runner.setHighlight(true);
        runner.setBaseURL(wsr.getBaseURL());
        execute("highlight");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue48() throws IllegalArgumentException {
        execute("issue48");
        assertThat(result.isAborted(), is(false));
        assertThat(result.isFailed(), is(false));
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void issue49_50() {
        execute("issue49+50");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue55() {
        execute("issue55");
        assertThat(result, is(instanceOf(Success.class)));
    }

    @Test
    public void issue76() {
        execute("issue76");
        assertThat(result, is(instanceOf(Success.class)));
    }
}
