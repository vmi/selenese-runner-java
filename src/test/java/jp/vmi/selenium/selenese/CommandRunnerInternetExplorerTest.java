package jp.vmi.selenium.selenese;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.Platform;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.IEDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverFactory;

public class CommandRunnerInternetExplorerTest extends CommandRunnerTest {
    @Override
    protected WebDriverFactory getWebDriverFactory() throws InvalidConfigurationException {
        return WebDriverFactory.getFactory(IEDriverFactory.class, new DriverOptions());
    }

    @Before
    public void checkPlatform() {
        Assume.assumeTrue(isSupportedPlatform());
    }

    private boolean isSupportedPlatform() {
        Platform current = Platform.getCurrent();
        return Platform.WINDOWS.is(current);
    }
}
