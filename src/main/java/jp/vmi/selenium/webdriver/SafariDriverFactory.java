package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.InvalidConfigurationException;

public class SafariDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(SafariDriverFactory.class);

    SafariDriverFactory(DriverOptions options) throws InvalidConfigurationException {
        super(options);
    }

    @Override
    protected DesiredCapabilities defaultCapabilities() {
        return DesiredCapabilities.safari();
    }

    @Override
    public WebDriver initDriver() {
        // FIXME SafariDriver don't receive capabilities...
        WebDriver driver = new SafariDriver();
        log.info("SafariDriver initialized.");
        return driver;
    }
}
