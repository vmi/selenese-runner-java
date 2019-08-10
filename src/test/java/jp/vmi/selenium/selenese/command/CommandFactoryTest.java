package jp.vmi.selenium.selenese.command;

import org.junit.Before;
import org.junit.Test;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Test for {@link CommandFactory}.
 */
public class CommandFactoryTest extends TestBase {

    private final Runner runner = new Runner();

    /**
     * setup WebDriverManager and Runner.
     */
    @Before
    public void setupWebDriverManager() {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        runner.setDriver(manager.get());
    }

    /**
     * Test of command "captureEntirePageScreenshot".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void captureEntirePageScreenshot() throws IllegalArgumentException {
        CommandFactory factory = runner.getCommandFactory();
        factory.newCommand(1, "captureEntirePageScreenshot");
    }

    /**
     * Test of command "deleteAllVisibleCookies".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void deleteAllVisibleCookies() throws IllegalArgumentException {
        CommandFactory factory = runner.getCommandFactory();
        factory.newCommand(1, "deleteAllVisibleCookies");
    }

    /**
     * Test of command "runScript".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void runScript() throws IllegalArgumentException {
        CommandFactory factory = runner.getCommandFactory();
        factory.newCommand(1, "runScript", "alert('test')");
    }

    /**
     * Test of command "type".
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void type() throws IllegalArgumentException {
        CommandFactory factory = runner.getCommandFactory();
        factory.newCommand(1, "type", "aaa", "");
    }

    /**
     * Test of commented out command.
     *
     * @throws IllegalArgumentException exception.
     */
    @Test
    public void commentedOut() throws IllegalArgumentException {
        CommandFactory factory = runner.getCommandFactory();
        ICommand cmd = factory.newCommand(1, "//open", "http://localhost/", "");
        assertThat(cmd, is(instanceOf(Echo.class)));
    }
}
