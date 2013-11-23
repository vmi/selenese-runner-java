package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class RunnerTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void proxyOption() throws IllegalArgumentException, IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        CommandLine cli = new Main().parseCommandLine(new String[] { "--proxy", "proxy.example.com", tmp.getAbsolutePath() });
        DriverOptions opt = new DriverOptions(cli);
        assertThat(opt.get(DriverOption.PROXY), is("proxy.example.com"));
    }

    @Test
    public void emptyFile() throws IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver());
        Result result = runner.run(tmp.getCanonicalPath());
        assertThat(result, is(instanceOf(Error.class)));
        assertThat(result.getMessage(), containsString("Not selenese script."));
    }

    @Test
    public void noSuchFile() throws IOException {
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver());
        Result result = runner.run("nosuchfile.html");
        assertTrue(result.isFailed());
    }

    @Test
    public void runFiles() throws IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver());
        runner.run(tmp.getPath(), tmp.getPath());
    }
}
