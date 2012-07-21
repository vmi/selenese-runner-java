package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IEDriverFactory extends WebDriverFactory {

    private static Logger log = LoggerFactory.getLogger(IEDriverFactory.class);

    // 参考: http://code.google.com/p/selenium/wiki/InternetExplorerDriver

    IEDriverFactory(DriverOptions options) throws IllegalArgumentException {
        super(options);
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
    }

    @Override
    protected DesiredCapabilities defaultCapabilities() {
        return DesiredCapabilities.internetExplorer();
    }

    @Override
    public WebDriver initDriver() {
        WebDriver driver = new InternetExplorerDriver(capabilities);
        log.info("IEDriver initialized.");
        return driver;
    }
}
