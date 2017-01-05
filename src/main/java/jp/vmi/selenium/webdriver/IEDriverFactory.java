package jp.vmi.selenium.webdriver;

import java.io.File;

import org.apache.commons.exec.OS;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import jp.vmi.selenium.selenese.utils.PathUtils;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link InternetExplorerDriver}.
 */
public class IEDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "ie";

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    @Override
    public boolean isProxySupported() {
        return false;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (!OS.isFamilyWindows())
            throw new UnsupportedOperationException("Unsupported platform: " + Platform.getCurrent());
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.internetExplorer(), driverOptions);
        if (driverOptions.has(IEDRIVER)) {
            String executable = PathUtils.normalize(driverOptions.get(IEDRIVER));
            if (!new File(executable).canExecute())
                throw new IllegalArgumentException("Missing IEDriverServer: " + executable);
            System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY, executable);
        }
        InternetExplorerDriverService service = new InternetExplorerDriverService.Builder()
            .usingAnyFreePort()
            .withEnvironment(driverOptions.getEnvVars())
            .build();
        caps.merge(driverOptions.getCapabilities());
        InternetExplorerDriver driver = new InternetExplorerDriver(service, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
