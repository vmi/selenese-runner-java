package jp.vmi.selenium.selenese;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

public class RunnerTest {

    @Test
    public void invalidProxyOption() throws InvalidOptionException, IOException {
        File tmp = File.createTempFile("aaa", "test.html");
        CommandLine cli = Runner.getCommandLine(new String[] { "--proxy", "proxy.example.com", tmp.getAbsolutePath() });
        DriverOptions opt = new DriverOptions(cli);
        assertEquals("proxy.example.com", opt.get(DriverOption.PROXY));
    }
}
