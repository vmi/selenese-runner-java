package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang.time.StopWatch;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Super class of test classes for running commands.
 */
public abstract class CommandRunnerTest {

    /**
     * Temprary directory.
     */
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    protected final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    protected abstract void setupWebDriverManager();

    /**
     * Setup WebDriverManager.
     */
    @Before
    public void initBefore() {
        setupWebDriverManager();
    }

    /**
     * Test of "CommandRunnerTestSimple.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testSimple() throws IllegalArgumentException {
        String script = TestUtils.getScriptFile(CommandRunnerTest.class, "Simple");
        Runner runner = new Runner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot().getPath());
        runner.setScreenshotAllDir(tmpDir.getRoot().getPath());
        runner.run(script);

        assertEquals(5, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestError.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testFailSubmit() throws IllegalArgumentException {
        String script = TestUtils.getScriptFile(CommandRunnerTest.class, "Error");
        Runner runner = new Runner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot().getPath());
        runner.setScreenshotAllDir(tmpDir.getRoot().getPath());
        runner.run(script);

        assertEquals(3, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestAssertFail.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testAssertFail() throws IllegalArgumentException {
        String script = TestUtils.getScriptFile(CommandRunnerTest.class, "AssertFail");
        Runner runner = new Runner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot().getPath());
        runner.setScreenshotAllDir(tmpDir.getRoot().getPath());
        runner.run(script);

        assertEquals(5, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestFlowControl.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testFlowControl() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "FlowControl"));

        assertEquals(28, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestForEach.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void testForEach() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "ForEach"));

        assertEquals(18, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    protected void execute(String scriptName) {
        Runner runner = new Runner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot().getPath());
        runner.setScreenshotAllDir(tmpDir.getRoot().getPath());
        runner.run(scriptName);
    }

    /**
     * Test of "CommandRunnerTestNoCommand.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void noCommandSelenese() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "NoCommand"));

        assertEquals(0, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    /**
     * Test of "CommandRunnerTestInvalidCommand.html".
     *
     * @throws IllegalArgumentException exception
     */
    @Test(expected = SeleniumException.class)
    public void invalidCommandInHtml() throws IllegalArgumentException {
        String script = TestUtils.getScriptFile(CommandRunnerTest.class, "InvalidCommand");
        Runner runner = new Runner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.run(script);
    }

    /**
     * Test of invalid command.
     *
     * @throws IllegalArgumentException exception
     */
    @Test(expected = SeleniumException.class)
    public void invalidCommand() throws IllegalArgumentException {
        WebDriver driver = WebDriverManager.getInstance().get();
        Runner runner = new Runner();
        runner.setDriver(driver);
        TestCase testCase = Binder.newTestCase(null, "invalidCommand", runner, "");
        CommandFactory commandFactory = new CommandFactory(testCase.getProc());
        Command invalidCommand = commandFactory.newCommand(1, "invalidCommand");
        testCase.addCommand(invalidCommand);
        testCase.execute(null);
    }

    /**
     * Test of capture screenshot.
     *
     * @throws IllegalArgumentException exception
     */
    @Test
    public void capture() throws IllegalArgumentException {
        final String filename = "test.png";
        File pngFile = new File(tmpDir.getRoot(), filename);
        if (pngFile.exists())
            pngFile.delete();
        WebDriver driver = WebDriverManager.getInstance().get();
        Runner runner = new Runner();
        runner.setDriver(driver);
        TestCase testCase = Binder.newTestCase(null, "capture", runner, "");
        CommandFactory commandFactory = new CommandFactory(testCase.getProc());
        Command captureCommand = commandFactory.newCommand(1, "captureEntirePageScreenshot", pngFile.getAbsolutePath());
        testCase.addCommand(captureCommand);
        testCase.execute(null);
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
        WebDriver driver = WebDriverManager.getInstance().get();
        Runner runner = new Runner();
        runner.setDriver(driver);
        TestCase testCase = Binder.newTestCase(null, "pauseCommand", runner, "");
        CommandFactory commandFactory = new CommandFactory(testCase.getProc());
        Command pause = commandFactory.newCommand(1, "pause", "5000");
        testCase.addCommand(pause);
        StopWatch sw = new StopWatch();
        sw.start();
        testCase.execute(null);
        sw.stop();
        assertThat(sw.getTime(), is(greaterThanOrEqualTo(5000L)));
    }

    /**
     * Test of "basic auth access"
     */
    @Test
    public void basicauth() {
        String script = TestUtils.getScriptFile(CommandRunnerTest.class, "BasicAuth");

        WebServer webserver = new WebServer();
        webserver.start();

        Runner runner = new Runner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setBaseURL("http://user:pass@" + webserver.getServerNameString() + "/");
        Result result = runner.run(script);
        assertThat(result.isSuccess(), is(true));

        webserver.stop();
    }
}
