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

    // see: http://code.google.com/p/selenium/wiki/InternetExplorerDriver

    private static final String IE_DRIVER_SERVER_EXE = "IEDriverServer.exe";

    @Override
    public boolean isProxySupported() {
        return false;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (!OS.isFamilyWindows())
            throw new UnsupportedOperationException("Unsupported platform: " + Platform.getCurrent());
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.internetExplorer(), driverOptions);
        File executable;
        if (driverOptions.has(IEDRIVER)) {
            executable = new File(driverOptions.get(IEDRIVER));
            if (!executable.canExecute())
                throw new IllegalArgumentException("Missing " + IE_DRIVER_SERVER_EXE + ": " + executable);
        } else {
            executable = PathUtils.searchExecutableFile(IE_DRIVER_SERVER_EXE);
            if (executable == null)
                throw new IllegalStateException("Missing " + IE_DRIVER_SERVER_EXE + " in PATH");
        }
        InternetExplorerDriverService service = new InternetExplorerDriverService.Builder()
            .usingAnyFreePort()
            .usingDriverExecutable(executable)
            .build();
        caps.merge(driverOptions.getCapabilities());
        InternetExplorerDriver driver = new InternetExplorerDriver(service, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
