package jp.vmi.selenium.webdriver;

import org.apache.commons.exec.OS;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link EdgeDriver}.
 */
public class EdgeDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "edge";

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
    public static EdgeOptions newEdgeOptions(DriverOptions driverOptions) {
        EdgeOptions options = new EdgeOptions();
        Proxy proxy = newProxy(driverOptions);
        if (proxy != null)
            options.setProxy(proxy);
        return options;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (!OS.isFamilyWindows())
            throw new UnsupportedOperationException("Unsupported platform: " + Platform.getCurrent());
        EdgeDriverService service = setupBuilder(new EdgeDriverService.Builder(), driverOptions, EDGEDRIVER).build();
        EdgeOptions options = newEdgeOptions(driverOptions);
        options.merge(driverOptions.getCapabilities());
        EdgeDriver driver = new EdgeDriver(service, options);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
