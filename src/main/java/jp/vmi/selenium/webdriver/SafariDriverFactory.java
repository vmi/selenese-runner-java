package jp.vmi.selenium.webdriver;

import org.apache.commons.exec.OS;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;

/**
 * Factory of {@link SafariDriver}.
 */
public class SafariDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "safari";

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    @Override
    public boolean isProxySupported() {
        return false;
    }

    /**
     * Create and initialize SafariOptions.
     *
     * @param driverOptions driver options.
     * @return SafariOptions.
     */
    public static SafariOptions newSafariOptions(DriverOptions driverOptions) {
        SafariOptions options = new SafariOptions();
        Proxy proxy = newProxy(driverOptions);
        if (proxy != null)
            options.setProxy(proxy); // SafariDriver does not support proxy...
        return options;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (!OS.isFamilyMac())
            throw new UnsupportedOperationException("Unsupported platform: " + Platform.getCurrent());
        SafariDriverService service = setupBuilder(new SafariDriverService.Builder(), driverOptions, null).build();
        SafariOptions options = newSafariOptions(driverOptions);
        options.merge(driverOptions.getCapabilities());
        SafariDriver driver = new SafariDriver(service, options);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
