package jp.vmi.selenium.selenese;

import org.junit.Test;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.junit.Assert.*;

/**
 * Test for HtmlUnit.
 */
public class CommandRunnerHtmlUnitTest extends CommandRunnerTest {

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        manager.setDriverOptions(new DriverOptions());
    }

    @Override
    @Test
    public void testFlowControl() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "FlowControl"));
        assertEquals(0, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Override
    @Test
    public void testForEach() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "ForEach"));
        assertEquals(0, tmpDir.getRoot().listFiles(pngFilter).length);
    }

    @Override
    @Test
    public void verifyNotText() throws IllegalArgumentException {
        execute(TestUtils.getScriptFile(CommandRunnerTest.class, "VerifyNotText"));
        assertEquals(0, tmpDir.getRoot().listFiles(pngFilter).length);
    }
}
