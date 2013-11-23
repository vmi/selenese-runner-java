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
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.htmlUnit(), driverOptions);
        if (driverOptions.has(REMOTE_BROWSER))
            caps.setBrowserName(driverOptions.get(REMOTE_BROWSER));
        if (driverOptions.has(REMOTE_PLATFORM))
            caps.setCapability(CapabilityType.PLATFORM, driverOptions.get(REMOTE_PLATFORM));
        if (driverOptions.has(REMOTE_VERSION))
            caps.setCapability(CapabilityType.VERSION, driverOptions.get(REMOTE_VERSION));
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
        RemoteWebDriver driver = new RemoteWebDriver(url, caps);
        log.info("Session ID: " + driver.getSessionId());
        return driver;
    }
}
