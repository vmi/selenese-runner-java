package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.URL;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

import static org.junit.Assert.*;

public abstract class CommandRunnerTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private final FilenameFilter pngFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".png");
        }
    };

    protected abstract WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException;

    protected static String getScriptName(String name) {
        Class<CommandRunnerTest> c = CommandRunnerTest.class;
        String html = "/" + c.getCanonicalName().replace('.', '/') + name + ".html";
        URL resource = c.getResource(html);
        if (resource == null) {
            throw new RuntimeException(new FileNotFoundException(html));
        }
        return c.getResource(html).toString();
    }

    @Test
    public void testSimple() throws InvalidConfigurationException {
        String script = getScriptName("Simple");
        Runner runner = new Runner(getWebDriverFactory());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(5, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void testFlowControl() throws InvalidConfigurationException {
        String script = getScriptName("FlowControl");
        Runner runner = new Runner(getWebDriverFactory());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(28, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void testForEach() throws InvalidConfigurationException {
        String script = getScriptName("ForEach");
        Runner runner = new Runner(getWebDriverFactory());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(18, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test
    public void noCommandSelenese() throws InvalidConfigurationException {
        String script = getScriptName("NoCommand");
        Runner runner = new Runner(getWebDriverFactory());
        runner.setScreenshotDir(tmpDir.getRoot());
        runner.setScreenshotAll(true);
        runner.run(script);

        assertEquals(0, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Test(expected = SeleniumException.class)
    public void invalidCommandInHtml() throws InvalidConfigurationException {
        String script = getScriptName("InvalidCommand");
        Runner runner = new Runner(getWebDriverFactory());
        runner.run(script);
    }

    @Test(expected = SeleniumException.class)
    public void invalidCommand() throws InvalidConfigurationException {
        WebDriver driver = getWebDriverFactory().get();
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("", driver);
        CommandFactory commandFactory = new CommandFactory(proc);
        Command invalidCommand = commandFactory.newCommand(1, "invalidCommand");
        Runner runner = new Runner(driver);
        Context context = new Context(proc);
        runner.run(context, invalidCommand);
    }

    @Test
    public void capture() throws InvalidConfigurationException {
        final String filename = "test.png";
        File pngFile = new File(tmpDir.getRoot(), filename);
        if (pngFile.exists()) {
            pngFile.delete();
        }

        WebDriver driver = getWebDriverFactory().get();
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("", driver);
        CommandFactory commandFactory = new CommandFactory(proc);
        Command captureCommand = commandFactory.newCommand(1, "captureEntirePageScreenshot", pngFile.getAbsolutePath());
        Runner runner = new Runner(driver);
        Context context = new Context(proc);
        runner.run(context, captureCommand);

        if (driver instanceof TakesScreenshot) {
            assertTrue(pngFile.exists());
        }
    }

}
