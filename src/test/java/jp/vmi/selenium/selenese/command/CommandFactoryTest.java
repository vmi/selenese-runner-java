package jp.vmi.selenium.selenese.command;

import org.junit.Before;
import org.junit.Test;

import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for {@link CommandFactory}.
 */
public class CommandFactoryTest {

    private final WebDriverManager manager = WebDriverManager.getInstance();

    /**
     * setup WebDriverManager.
     */
    @Before
    public void setupWebDriverManager() {
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        manager.setDriverOptions(new DriverOptions());
    }

    /**
     * Test of command "captureEntirePageScreenshot".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void captureEntirePageScreenshot() throws IllegalArgumentException {
        CustomCommandProcessor proc = new CustomCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory();
        factory.setProc(proc);
        factory.newCommand(1, "captureEntirePageScreenshot");
    }

    /**
     * Test of command "deleteAllVisibleCookies".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void deleteAllVisibleCookies() throws IllegalArgumentException {
        CustomCommandProcessor proc = new CustomCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory();
        factory.setProc(proc);
        factory.newCommand(1, "deleteAllVisibleCookies");
    }

    /**
     * Test of command "runScript".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void runScript() throws IllegalArgumentException {
        CustomCommandProcessor proc = new CustomCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory();
        factory.setProc(proc);
        factory.newCommand(1, "runScript", "alert('test')");
    }

    /**
     * Test of command "type".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void type() throws IllegalArgumentException {
        CustomCommandProcessor proc = new CustomCommandProcessor("http://localhost/", manager.get());
        CommandFactory factory = new CommandFactory();
        factory.setProc(proc);
        factory.newCommand(1, "type", "aaa", "");
    }
}
