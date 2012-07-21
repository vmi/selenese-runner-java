package jp.vmi.selenium.selenese;

import org.junit.Ignore;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.SafariDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

@Ignore("not yet ready to safari test.")
public class CommandRunnerSafariTest extends CommandRunnerTest {
    @Override
    protected WebDriverFactory getWebDriverFactory() throws IllegalArgumentException {
        return WebDriverFactory.getFactory(SafariDriverFactory.class, new DriverOptions());
    }
}
