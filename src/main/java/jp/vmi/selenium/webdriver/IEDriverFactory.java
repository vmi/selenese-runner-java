package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

/**
 * Factory of {@link InternetExplorerDriver}.
 */
public class IEDriverFactory extends WebDriverFactory {

    private static Logger log = LoggerFactory.getLogger(IEDriverFactory.class);

    // 参考: http://code.google.com/p/selenium/wiki/InternetExplorerDriver

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        Platform platform = Platform.getCurrent();
        log.info("Platform: {}", platform);
        switch (platform) {
        case WINDOWS:
        case XP:
        case VISTA:
            break;
        default:
            throw new UnsupportedOperationException("Unsupported platform: " + platform);
        }
        // DesiredCapabilities capabilities = setupProxy(DesiredCapabilities.internetExplorer(), driverOptions);
        // return new InternetExplorerDriver(capabilities);
        if (driverOptions.has(DriverOption.PROXY))
            log.warn("No support proxy with InternetExprolerDriver. Please set proxy to IE in advance.");
        return new InternetExplorerDriver();

    }
}
