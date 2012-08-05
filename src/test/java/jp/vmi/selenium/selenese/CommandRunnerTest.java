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
import org.openqa.selenium.WebDriverCommandProcessor;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public abstract class CommandRunnerTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    protected final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    protected abstract void setupWebDriverManager();

    @Before
    public void initBefore() {
        setupWebDriverManager();
    }

    @Test
    public void testSimple() throws IllegalArgumentException {
        File script = TestUtils.getScriptFile(CommandRunnerTest.class, "Simple");
        Runner runner = Binder.getRunner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(5, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void testFailSubmit() throws IllegalArgumentException {
        File script = TestUtils.getScriptFile(CommandRunnerTest.class, "Error");
        Runner runner = Binder.getRunner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(3, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void testAssertFail() throws IllegalArgumentException {
        File script = TestUtils.getScriptFile(CommandRunnerTest.class, "AssertFail");
        Runner runner = Binder.getRunner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(5, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void testFlowControl() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "FlowControl"));

        assertEquals(28, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void testForEach() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "ForEach"));

        assertEquals(18, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    protected void execute(File scriptName) {
        Runner runner = Binder.getRunner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(scriptName);
    }

    @Test
    public void noCommandSelenese() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "NoCommand"));

        assertEquals(0, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test(expected = SeleniumException.class)
    public void invalidCommandInHtml() throws IllegalArgumentException {
        File script = TestUtils.getScriptFile(CommandRunnerTest.class, "InvalidCommand");
        Runner runner = Binder.getRunner();
        runner.setDriver(WebDriverManager.getInstance().get());
        runner.run(script);
    }

    @Test(expected = SeleniumException.class)
    public void invalidCommand() throws IllegalArgumentException {
        WebDriver driver = WebDriverManager.getInstance().get();
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("", driver);
        CommandFactory commandFactory = new CommandFactory(proc);
        Command invalidCommand = commandFactory.newCommand(1, "invalidCommand");
        Runner runner = Binder.getRunner();
        runner.setDriver(driver);
        Context context = new Context(proc);
        runner.evaluate(context, invalidCommand);
    }

    @Test
    public void capture() throws IllegalArgumentException {
        final String filename = "test.png";
        File pngFile = new File(tmpDir.getRoot(), filename);
        if (pngFile.exists()) {
            pngFile.delete();
        }

        WebDriver driver = WebDriverManager.getInstance().get();
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("", driver);
        CommandFactory commandFactory = new CommandFactory(proc);
        Command captureCommand = commandFactory.newCommand(1, "captureEntirePageScreenshot", pngFile.getAbsolutePath());
        Runner runner = Binder.getRunner();
        runner.setDriver(driver);
        Context context = new Context(proc);
        runner.evaluate(context, captureCommand);

        if (driver instanceof TakesScreenshot) {
            assertTrue(pngFile.exists());
        }
    }

    @Test
    public void pauseCommand() throws IllegalArgumentException {
        WebDriver driver = WebDriverManager.getInstance().get();
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("",
            driver);
        CommandFactory commandFactory = new CommandFactory(proc);
        Command pause = commandFactory.newCommand(1, "pause", "5000");
        Runner runner = Binder.getRunner();
        runner.setDriver(driver);
        Context context = new Context(proc);

        StopWatch sw = new StopWatch();
        sw.start();
        runner.evaluate(context, pause);
        sw.stop();
        assertThat(sw.getTime(), is(greaterThanOrEqualTo(5000L)));
    }
}
