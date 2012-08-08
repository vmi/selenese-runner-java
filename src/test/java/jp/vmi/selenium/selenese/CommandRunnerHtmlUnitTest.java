package jp.vmi.selenium.selenese;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

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
    public void testSimple() {
        //no test
        //original test is occur javascript error on htmlunit.
        throw new AssumptionViolatedException("HtmlUnitDriver is not supported google javascript");
    }

    @Override
    @Test
    public void testFailSubmit() throws IllegalArgumentException {
        //no test
        //original test is occur javascript error on htmlunit.
        throw new AssumptionViolatedException("HtmlUnitDriver is not supported google javascript");
    }

    @Override
    @Test
    public void testAssertFail() throws IllegalArgumentException {
        //no test
        //original test is occur javascript error on htmlunit.
        throw new AssumptionViolatedException("HtmlUnitDriver is not supported google javascript");
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

}
