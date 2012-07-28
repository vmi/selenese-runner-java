package jp.vmi.selenium.selenese.command;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.firefox.FirefoxBinary;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandFactoryTest {

    private final WebDriverManager manager = WebDriverManager.getInstance();

    @Before
    public void assumeInstalledFirefox() {
        try {
            new FirefoxBinary();
        } catch (SeleniumException e) {
            Assume.assumeNoException(e);
        }
    }

    @Before
    public void setupWebDriverManager() {
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
    }

    @Test
    public void captureEntirePageScreenshot() throws IllegalArgumentException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "captureEntirePageScreenshot");
    }

    @Test
    public void deleteAllVisibleCookies() throws IllegalArgumentException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "deleteAllVisibleCookies");
    }

    @Test
    public void runScript() throws IllegalArgumentException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "runScript", "alert('test')");
    }

    @Test
    public void type() throws IllegalArgumentException {
        WebDriverCommandProcessor proc = new WebDriverCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory(proc);
        factory.newCommand(1, "type", "aaa", "");
    }
}
