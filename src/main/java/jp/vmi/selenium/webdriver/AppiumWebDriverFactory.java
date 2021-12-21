package jp.vmi.selenium.webdriver;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link RemoteWebDriver}.
 */
public class AppiumWebDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "appium";

    private static final Logger log = LoggerFactory.getLogger(AppiumWebDriverFactory.class);

    /**
     * Appium driver.
     */
    public static class AppiumDriver extends RemoteWebDriver {

        /**
         * Constructor.
         *
         * @param url remote url.
         * @param caps capabilities.
         */
        public AppiumDriver(URL url, Capabilities caps) {
            super(url, caps);
        }
    }

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(new DesiredCapabilities(), driverOptions);
        // Set capabilities according to https://github.com/appium/appium/blob/master/docs/caps.md
        caps.setBrowserName("");
        if (driverOptions.has(REMOTE_BROWSER))
            caps.setCapability("app", driverOptions.get(REMOTE_BROWSER));
        if (driverOptions.has(REMOTE_PLATFORM))
            caps.setCapability("device", driverOptions.get(REMOTE_PLATFORM));
        if (driverOptions.has(REMOTE_VERSION))
            caps.setCapability(CapabilityType.BROWSER_VERSION, driverOptions.get(REMOTE_VERSION));
        URL url;
        if (driverOptions.has(REMOTE_URL)) {
            try {
                url = new URL(driverOptions.get(REMOTE_URL));
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid --remote-url: " + e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Require --remote-url to know where to connect to");
        }
        caps.merge(driverOptions.getCapabilities());
        RemoteWebDriver driver = new AppiumDriver(url, caps);
        log.info("Session ID: " + driver.getSessionId());
        return driver;
    }
}
