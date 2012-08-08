package jp.vmi.selenium.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Abstract class of factory for {@link WebDriver}s.
 */
public abstract class WebDriverFactory {

    protected DesiredCapabilities setupProxy(DesiredCapabilities capabilities, DriverOptions driverOptions) {
        if (driverOptions.has(PROXY)) {
            Proxy proxy = new Proxy();
            proxy.setProxyType(ProxyType.MANUAL);
            String ps = driverOptions.get(PROXY);
            proxy.setHttpProxy(ps)
                .setSslProxy(ps)
                .setFtpProxy(ps);
            if (driverOptions.has(NO_PROXY))
                proxy.setNoProxy(driverOptions.get(NO_PROXY));
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        }
        return capabilities;
    }

    /**
     * Create new WebDriver instance with driver options.
     *
     * @param driverOptions driver options.
     * @return WebDriver instance.
     */
    public abstract WebDriver newInstance(DriverOptions driverOptions);
}
