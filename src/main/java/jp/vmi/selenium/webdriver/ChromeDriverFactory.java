package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.InvalidConfigurationException;

/*
 * see: http://code.google.com/p/chromedriver/
 */
public class ChromeDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(ChromeDriverFactory.class);

    ChromeDriverFactory(DriverOptions options) throws InvalidConfigurationException {
        super(options);
    }

    @Override
    protected DesiredCapabilities defaultCapabilities() {
        return DesiredCapabilities.chrome();
    }

    @Override
    public WebDriver initDriver() {
        // new ChromeDriver(Capabilities) is deprecated...
        ChromeOptions options = new ChromeOptions();
        Proxy proxy = (Proxy) capabilities.getCapability(CapabilityType.PROXY);
        if (proxy != null)
            options.addArguments("--proxy-server=http://" + proxy.getHttpProxy());
        WebDriver driver = new ChromeDriver(options);
        log.info("ChromeDriver initialized.");
        return driver;
    }
}
