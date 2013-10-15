package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.testutils.TestUtils;
import jp.vmi.selenium.testutils.WebServerResouce;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * test for running commands.
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class DriverDependentTest extends TestBase {

    @Parameters(name = "{index}: {0}")
    public static List<Object[]> getWebDriverFactories() {
        return TestUtils.getWebDriverFactories();
    }

    @Rule
    public final WebServerResouce wsr = new WebServerResouce();

    @Rule
    public final TemporaryFolder screenshotDir = new TemporaryFolder();

    @Rule
    public final TemporaryFolder screenshotOnFailDir = new TemporaryFolder();

    @Parameter
    public WebDriverFactory factory;

    public WebDriver driver;

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

    private Runner newRunner() {
        Runner runner = new Runner();
        runner.setDriver(driver);
        runner.setScreenshotDir(screenshotDir.getRoot().getPath());
        runner.setScreenshotAllDir(screenshotDir.getRoot().getPath());
        runner.setScreenshotOnFailDir(screenshotOnFailDir.getRoot().getPath());
        return runner;
    }

    /**
     * Test of "CommandRunnerTestSimple.html".
     *
     * @throws IllegalArgumentException exception
     * @throws IOException exception
     */
    @Test
    public void testSimple() throws IllegalArgumentException, IOException {
        String script = TestUtils.getScriptFile(DriverDependentTest.class, "Simple");
        Runner runner = newRunner();
        runner.run(script);
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
        String script = TestUtils.getScriptFile(DriverDependentTest.class, "Error");
        Runner runner = newRunner();
        runner.run(script);
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
        String script = TestUtils.getScriptFile(DriverDependentTest.class, "AssertFail");
        Runner runner = newRunner();
        runner.run(script);
        if (runner.getDriver() instanceof TakesScreenshot) {
            assertEquals(1, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
            assertEquals(5, screenshotDir.getRoot().listFiles(pngFilter).length);
        }
    }

    /**
     * Test of "CommandRunnerTestFlowControl.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testFlowControl() throws IllegalArgumentException {
        String result = execute(TestUtils.getScriptFile(DriverDependentTest.class, "FlowControl"));
        assertThat(result, containsString("[Finished with x = 15 and y = 14]"));
    }

    /**
     * Test of "CommandRunnerTestForEach.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testForEach() throws IllegalArgumentException {
        String result = execute(TestUtils.getScriptFile(DriverDependentTest.class, "ForEach"));
        assertThat(result, containsString("[chedder]"));
        assertThat(result, containsString("[edam]"));
        assertThat(result, containsString("[swiss]"));
    }

    protected String execute(String scriptName) {
        Runner runner = newRunner();
        String tmpPath = screenshotDir.getRoot().getPath();
        runner.setScreenshotDir(tmpPath);
        runner.setScreenshotAllDir(tmpPath);
        runner.setScreenshotOnFailDir(screenshotOnFailDir.getRoot().getPath());
        runner.setJUnitResultDir(tmpPath);
        try {
            runner.run(scriptName);
            return FileUtils.readFileToString(new File(tmpPath, "TEST-default-00.xml"), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            runner.setJUnitResultDir(null);
        }
    }

    /**
     * Test of "CommandRunnerTestNoCommand.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void noCommandSelenese() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(DriverDependentTest.class, "NoCommand"));

        assertEquals(0, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
        assertEquals(0, screenshotDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestNoCommand.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void verifyNotText() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(DriverDependentTest.class, "VerifyNotText"));

        assertEquals(1, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
        assertEquals(3, screenshotDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestInvalidCommand.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test(expected = SeleniumException.class)
    public void invalidCommandInHtml() throws IllegalArgumentException {
        String script = TestUtils.getScriptFile(DriverDependentTest.class, "InvalidCommand");
        Runner runner = newRunner();
        try {
            runner.run(script);
        } finally {
            assertEquals(0, screenshotOnFailDir.getRoot().listFiles(pngFilter).length);
        }
    }

    /**
     * Test of invalid command.
     *
     * @throws IllegalArgumentException exception
     */
    @Test(expected = SeleniumException.class)
    public void invalidCommand() throws IllegalArgumentException {
        Runner runner = newRunner();
        TestCase testCase = Binder.newTestCase(null, "invalidCommand", runner, ws.getUrl());
        CommandFactory commandFactory = new CommandFactory();
        commandFactory.setProc(testCase.getProc());
        Command invalidCommand = commandFactory.newCommand(1, "invalidCommand");
        testCase.addCommand(invalidCommand);
        testCase.execute(null, runner);
    }

    /**
     * Test of issue #48.
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void issue48() throws IllegalArgumentException {
        Assume.assumeThat(WebDriverManager.getInstance().get(), instanceOf(FirefoxDriver.class));
        String script = TestUtils.getScriptFile(DriverDependentTest.class, "Issue48");
        Runner runner = newRunner();
        Result result = runner.run(script);
        assertThat(result.isAborted(), is(false));
        assertThat(result.isFailed(), is(false));
        assertThat(result.isSuccess(), is(true));
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
        Runner runner = newRunner();
        TestCase testCase = Binder.newTestCase(null, "capture", runner, ws.getUrl());
        CommandFactory commandFactory = new CommandFactory();
        commandFactory.setProc(testCase.getProc());
        Command captureCommand = commandFactory.newCommand(1, "captureEntirePageScreenshot", pngFile.getAbsolutePath());
        testCase.addCommand(captureCommand);
        testCase.execute(null, runner);
        if (driver instanceof TakesScreenshot)
            assertTrue(pngFile.exists());
    }

    /**
     * Test of "pauseCommand".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void pauseCommand() throws IllegalArgumentException {
        Runner runner = newRunner();
        TestCase testCase = Binder.newTestCase(null, "pauseCommand", runner, ws.getUrl());
        CommandFactory commandFactory = new CommandFactory();
        commandFactory.setProc(testCase.getProc());
        Command pause = commandFactory.newCommand(1, "pause", "5000");
        testCase.addCommand(pause);
        StopWatch sw = new StopWatch();
        sw.start();
        testCase.execute(null, runner);
        sw.stop();
        assertThat(sw.getTime(), is(greaterThanOrEqualTo(4900L)));
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

        String script = TestUtils.getScriptFile(DriverDependentTest.class, "BasicAuth");

        Runner runner = newRunner();
        runner.setBaseURL("http://user:pass@" + ws.getServer().getServerNameString() + "/");
        Result result = runner.run(script);
        assertThat(result.isSuccess(), is(true));
    }
}
