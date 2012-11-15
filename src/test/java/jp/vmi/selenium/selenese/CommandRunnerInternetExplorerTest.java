package jp.vmi.selenium.selenese;

import java.io.File;

import org.junit.Assume;
import org.junit.Before;
import org.openqa.selenium.Platform;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Test for Internet Explorer.
 */
public class CommandRunnerInternetExplorerTest extends CommandRunnerTest {

    private static final String EXE = "IEDriverServer.exe";

    /**
     * Check supported platform.
     */
    @Before
    public void checkPlatform() {
        Assume.assumeTrue(isSupportedPlatform());
    }

    private boolean isSupportedPlatform() {
        Platform current = Platform.getCurrent();
        return Platform.WINDOWS.is(current);
    }

    private String findIEDriverServer() {
        File ieds = new File(System.getProperty("user.dir"), EXE);
        if (ieds.canExecute())
            return ieds.getPath();
        return null;
    }

    @Override
    protected void setupWebDriverManager() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.IE);
        DriverOptions opts = new DriverOptions();
        String ieds = findIEDriverServer();
        if (ieds != null)
            opts.set(DriverOption.IEDRIVER, ieds);
        manager.setDriverOptions(opts);
    }
}
