package jp.vmi.selenium.webdriver;

import org.apache.commons.exec.OS;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.ie.InternetExplorerOptions;

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

    /**
     * Create and initialize InternetExplorerOptions.
     *
     * @param driverOptions driver options.
     * @return InternetExplorerOptions.
     */
    public static InternetExplorerOptions newInternetExplorerOptions(DriverOptions driverOptions) {
        InternetExplorerOptions options = new InternetExplorerOptions();
        Proxy proxy = newProxy(driverOptions);
        if (proxy != null)
            options.setProxy(proxy);
        return options;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (!OS.isFamilyWindows())
            throw new UnsupportedOperationException("Unsupported platform: " + Platform.getCurrent());
        InternetExplorerDriverService service = setupBuilder(new InternetExplorerDriverService.Builder(), driverOptions, IEDRIVER).build();
        InternetExplorerOptions options = newInternetExplorerOptions(driverOptions);
        options.merge(driverOptions.getCapabilities());
        InternetExplorerDriver driver = new InternetExplorerDriver(service, options);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
