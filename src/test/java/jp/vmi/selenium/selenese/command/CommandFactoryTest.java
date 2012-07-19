package jp.vmi.selenium.selenese.command;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;

import jp.vmi.selenium.selenese.InvalidConfigurationException;
import jp.vmi.selenium.webdriver.BrowserNotFoundException;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandFactoryTest {

    @Before
    public void assumeInstalledFirefox() throws InvalidConfigurationException {
        try {
            FirefoxDriverFactory f = new FirefoxDriverFactory(new DriverOptions());
            f.initDriver();
        } catch (BrowserNotFoundException e) {
            Assume.assumeNoException(e);
        }
    }

    @Test
    public void captureEntirePageScreenshot() throws InvalidConfigurationException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", WebDriverFactory.getFactory(
            FirefoxDriverFactory.class, new DriverOptions()).get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "captureEntirePageScreenshot");
    }

    @Test
    public void deleteAllVisibleCookies() throws InvalidConfigurationException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", WebDriverFactory.getFactory(
            FirefoxDriverFactory.class, new DriverOptions()).get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "deleteAllVisibleCookies");
    }

    @Test
    public void runScript() throws InvalidConfigurationException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", WebDriverFactory.getFactory(
            FirefoxDriverFactory.class, new DriverOptions()).get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "runScript", "alert('test')");
    }

    @Test
    public void type() throws InvalidConfigurationException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", WebDriverFactory.getFactory(
            FirefoxDriverFactory.class, new DriverOptions()).get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "type", "aaa", "");
    }
}
