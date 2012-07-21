package jp.vmi.selenium.selenese;

import org.junit.Test;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.HtmlUnitDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

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
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void testFlowControl() throws IllegalArgumentException {
        super.testFlowControl();
    }

    @Override
    @Test(expected = UnsupportedOperationException.class)
    public void testForEach() throws IllegalArgumentException {
        super.testForEach();
    }

}
