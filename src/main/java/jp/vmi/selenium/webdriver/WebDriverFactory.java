package jp.vmi.selenium.webdriver;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Abstract class of factory for {@link WebDriver}s.
 */
public abstract class WebDriverFactory {

    static {
        LoggerUtils.initLogger();
    }

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    Map<String, String> environmentVariables = new HashMap<String, String>();

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
     * Get environmnent variable map.
     *
     * @return environment variable map.
     */
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    /**
     * Create new WebDriver instance with driver options.
     *
     * @param driverOptions driver options.
     * @return WebDriver instance.
     */
    public abstract WebDriver newInstance(DriverOptions driverOptions);
}
