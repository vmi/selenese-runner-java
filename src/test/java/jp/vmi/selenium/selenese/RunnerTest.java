package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static org.junit.Assert.*;

public class RunnerTest {

    @Test
    public void invalidProxyOption() throws IllegalArgumentException, IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        CommandLine cli = new Main().parseCommandLine(new String[] { "--proxy", "proxy.example.com", tmp.getAbsolutePath() });
        DriverOptions opt = new DriverOptions(cli);
        assertEquals("proxy.example.com", opt.get(DriverOption.PROXY));
    }

    @Test
    public void emptyFile() throws IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        Runner runner = Binder.getRunner();
        runner.setDriver(new HtmlUnitDriver());
        runner.run(tmp.getAbsoluteFile());
    }
}
