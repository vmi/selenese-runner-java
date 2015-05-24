package jp.vmi.selenium.webdriver;

import java.net.MalformedURLException;
import java.net.URL;

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
public class RemoteWebDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(RemoteWebDriverFactory.class);

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = DesiredCapabilities.htmlUnit();
        setupProxy(caps, driverOptions);
        caps.merge(driverOptions.getCapabilities());
        if (driverOptions.has(REMOTE_BROWSER)) {
            String browser = driverOptions.get(REMOTE_BROWSER);
            caps.setBrowserName(browser);
            log.info("Remote browser: {}", browser);
            if ("firefox".equalsIgnoreCase(browser))
                FirefoxDriverFactory.setDriverSpecificCapabilities(caps, driverOptions, true);
            if ("chrome".equalsIgnoreCase(browser))
                ChromeDriverFactory.setDriverSpecificCapabilities(caps, driverOptions);
        }
        if (driverOptions.has(REMOTE_PLATFORM)) {
            String platform = driverOptions.get(REMOTE_PLATFORM);
            caps.setCapability(CapabilityType.PLATFORM, platform);
            log.info("Remote platform: {}", platform);
        }
        if (driverOptions.has(REMOTE_VERSION)) {
            String version = driverOptions.get(REMOTE_VERSION);
            caps.setCapability(CapabilityType.VERSION, version);
            log.info("Remote version: {}", version);
        }
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
        log.info("Remote URL: {}", url);
        RemoteWebDriver driver = new RemoteWebDriver(url, caps);
        log.info("Session ID: " + driver.getSessionId());
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
