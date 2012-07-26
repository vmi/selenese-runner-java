package jp.vmi.selenium.selenese;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.HtmlUnitDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

import static org.junit.Assert.*;

public class CommandRunnerHtmlUnitTest extends CommandRunnerTest {
    @Override
    protected WebDriverFactory getWebDriverFactory() throws IllegalArgumentException {
        return WebDriverFactory.getFactory(HtmlUnitDriverFactory.class, new DriverOptions());
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
