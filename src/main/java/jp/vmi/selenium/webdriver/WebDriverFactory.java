package jp.vmi.selenium.webdriver;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Abstract class of factoryName for {@link WebDriver}s.
 */
public abstract class WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    protected static final int DEFAULT_WIDTH = 1024;

    protected static final int DEFAULT_HEIGHT = 768;

    /**
     * Is proxy supported?
     *
     * @return true if proxy is supported by driver.
     */
    public boolean isProxySupported() {
        return true;
    }

    protected DesiredCapabilities setupProxy(DesiredCapabilities caps, DriverOptions driverOptions) {
        if (driverOptions.has(PROXY)) {
            if (!isProxySupported()) {
                log.warn("No support proxy with {}. Please set proxy to browser configuration in advance.",
                    getClass().getSimpleName().replaceFirst("Factory$", ""));
                return caps;
            }
            Proxy proxy = new Proxy();
            proxy.setProxyType(ProxyType.MANUAL);
            String ps = driverOptions.get(PROXY);
            proxy.setHttpProxy(ps)
                .setSslProxy(ps)
                .setFtpProxy(ps);
            if (driverOptions.has(NO_PROXY))
                proxy.setNoProxy(driverOptions.get(NO_PROXY));
            caps.setCapability(CapabilityType.PROXY, proxy);
        }
        return caps;
    }

    /**
     * Create new WebDriver instance with driver options.
     *
     * @param driverOptions driver options.
     * @return WebDriver instance.
     */
    public abstract WebDriver newInstance(DriverOptions driverOptions);

    protected void setInitialWindowSize(WebDriver driver, DriverOptions driverOptions) {
        Dimension size;
        if (driverOptions.has(WIDTH) || driverOptions.has(HEIGHT)) {
            int width = NumberUtils.toInt(driverOptions.get(WIDTH), DEFAULT_WIDTH);
            int height = NumberUtils.toInt(driverOptions.get(HEIGHT), DEFAULT_HEIGHT);
            size = new Dimension(width, height);
        } else {
            size = getDefaultWindowSize(driver);
            if (size == null) {
                log.info("Initial window size: system default");
                return;
            }
        }
        driver.manage().window().setSize(size);
        log.info("Initial window size: {}x{}", size.width, size.height);
    }

    protected Dimension getDefaultWindowSize(WebDriver driver) {
        // don't set window size without "--width" and/or "--height".
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || getClass() == obj.getClass();
    }
}
