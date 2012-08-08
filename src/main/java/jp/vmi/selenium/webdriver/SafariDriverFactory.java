package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

/**
 * Factory of {@link SafariDriver}.
 */
public class SafariDriverFactory extends WebDriverFactory {

    private static Logger log = LoggerFactory.getLogger(SafariDriverFactory.class);

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (driverOptions.has(DriverOption.PROXY))
            log.warn("No support proxy with SafariDriver. Please set proxy to Safari in advance.");
        return new SafariDriver();
    }
}
