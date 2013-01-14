package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Test for {@link Runner}.
 */
public class RunnerTest {
    /**
     * Temprary directory.
     */
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    /**
     * Test of --proxy option.
     *
     * @throws IllegalArgumentException exception.
     * @throws IOException exception.
     */
    @Test
    public void invalidProxyOption() throws IllegalArgumentException, IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        CommandLine cli = new Main().parseCommandLine(new String[] { "--proxy", "proxy.example.com", tmp.getAbsolutePath() });
        DriverOptions opt = new DriverOptions(cli);
        assertEquals("proxy.example.com", opt.get(DriverOption.PROXY));
    }

    /**
     * Test of empty file.
     *
     * @throws IOException exception.
     */
    @Test
    public void emptyFile() throws IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver());
        runner.run(tmp.getCanonicalPath());
    }

    /**
     * Test of no file.
     *
     * @throws IOException exception.
     */
    @Test
    public void nosuchFile() throws IOException {
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver());
        Result result = runner.run("nosuchfile.html");
        assertTrue(result.isFailed());
    }

    /**
     * Test of no file.
     *
     * @throws IOException exception.
     */
    @Test
    public void ignoreScreenshotCommand() throws IOException {
        // assumption installed firefox
        // TODO merge duplicated codes.
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        } catch (WebDriverException e) {
            Assume.assumeNoException(e);
        }
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
        try {
            WebDriverManager.getInstance().get();
        } catch (WebDriverException e) {
            if (e.getMessage().contains("no display specified")) {
                Assume.assumeNoException(e);
            }
        }

        Runner runner = new Runner();
        runner.setDriver(new FirefoxDriver());
        runner.setIgnoreScreenshotCommand(true);
        runner.setScreenshotDir(tmpDir.getRoot().getAbsolutePath());
        Result result = runner.run(getClass().getResource("RunnerTestIgnoreCapture.html").getPath());
        assertThat(FileUtils.listFiles(tmpDir.getRoot(), new String[] { "png" }, true).size(), is(0));
        assertThat(result.isSuccess(), is(true));
    }

    /**
     * Test of {@link Runner#run(String...)}).
     *
     * @throws IOException exception.
     */
    @Test
    public void runFiles() throws IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver());
        runner.run(tmp.getPath(), tmp.getPath());
    }
}
