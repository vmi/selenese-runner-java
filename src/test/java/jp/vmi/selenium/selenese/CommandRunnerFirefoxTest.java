package jp.vmi.selenium.selenese;

import org.junit.Rule;

import jp.vmi.selenium.testutils.AssumptionFirefox;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for Firefox.
 */
public class CommandRunnerFirefoxTest extends CommandRunnerTest {

    /**
     * check firefox
     */
    @Rule
    public AssumptionFirefox assumptionFirefox = new AssumptionFirefox();

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.FIREFOX);
        manager.setDriverOptions(new DriverOptions());
    }
}
