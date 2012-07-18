package jp.vmi.selenium.webdriver;

import java.io.IOException;

import jp.vmi.selenium.selenese.InvalidConfigurationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FirefoxDriverFactoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = BrowserNotFoundException.class)
    public void firefoxNotFoundIn_webdriver_firefox_bin() throws IOException, InvalidConfigurationException, BrowserNotFoundException {
        //TODO 定数化
        System.setProperty("webdriver.firefox.bin", folder.newFolder().getAbsolutePath());
        FirefoxDriverFactory f = new FirefoxDriverFactory(new DriverOptions());
        f.initDriver();
    }
}
