package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.Platform;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

public class CommandRunnerInternetExplorerTest extends CommandRunnerTest {

    @Before
    public void checkPlatform() {
        Assume.assumeTrue(isSupportedPlatform());
    }

    private boolean isSupportedPlatform() {
        Platform current = Platform.getCurrent();
        return Platform.WINDOWS.is(current);
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory("ie");
        manager.setDriverOptions(new DriverOptions());
    }
}
