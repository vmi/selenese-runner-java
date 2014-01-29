package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Factory of {@link AndroidDriver}.
 */
@Deprecated
public class AndroidDriverFactory extends WebDriverFactory {

    @Override
    public boolean isProxySupported() {
        return false;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = setupProxy(DesiredCapabilities.android(), driverOptions);
        caps.merge(driverOptions.getCapabilities());
        return new AndroidDriver(caps);
    }
}
