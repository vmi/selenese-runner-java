package jp.vmi.selenium.selenese;

import org.junit.Ignore;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

@Ignore("not yet ready to safari test.")
public class CommandRunnerSafariTest extends CommandRunnerTest {

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory("safari");
        manager.setDriverOptions(new DriverOptions());
    }
}
