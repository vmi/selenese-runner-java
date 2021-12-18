package jp.vmi.selenium.webdriver;

import java.io.File;
import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import jp.vmi.selenium.selenese.utils.PathUtils;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Abstract class of factoryName for {@link WebDriver}s.
 */
public abstract class WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    protected static final int DEFAULT_WIDTH = 1024;

    protected static final int DEFAULT_HEIGHT = 768;

    /**
     * Get browser name.
     *
     * @return browser name. (i.e. "firefox", "chrome", ...)
     */
    public String getBrowserName() {
        String name = getClass().getSimpleName();
        if (Strings.isNullOrEmpty(name))
            return "";
        else if (name.endsWith("WebDriverFactory"))
            return name.substring(0, name.length() - "WebDriverFactory".length()).toLowerCase();
        else if (name.endsWith("DriverFactory"))
            return name.substring(0, name.length() - "DriverFactory".length()).toLowerCase();
        else
            return name.toLowerCase();
    }

    /**
     * Is proxy supported?
     *
     * @return true if proxy is supported by driver.
     */
    public boolean isProxySupported() {
        return true;
    }

    /**
     * Setup builder for DriverService.
     *
     * @param builder DriverService builder.
     * @param driverOptions driver options.
     * @param driverKey driver option (e.g. '--firefox', '--chrome')
     * @param <B> subclass of DriverService.Builder
     * @return setup builder.
     */
    public static <B extends DriverService.Builder<?, B>> B setupBuilder(B builder, DriverOptions driverOptions, DriverOption driverKey) {
        builder = builder.usingAnyFreePort().withEnvironment(driverOptions.getEnvVars());
        if (driverKey != null && driverOptions.has(driverKey)) {
            File executable = new File(PathUtils.normalize(driverOptions.get(driverKey)));
            if (!executable.canExecute())
                throw new IllegalArgumentException("Missing driver executable: " + executable);
            builder = builder.usingDriverExecutable(executable);
        }
        return builder;
    }

    /**
     * Create new Proxy from driver options.
     *
     * @param driverOptions driver options.
     * @return Proxy or null.
     */
    public static Proxy newProxy(DriverOptions driverOptions) {
        if (!driverOptions.has(PROXY_TYPE) && !driverOptions.has(PROXY))
            return null;
        String proxyTypeStr = driverOptions.get(PROXY_TYPE);
        if (proxyTypeStr == null)
            proxyTypeStr = "manual";
        ProxyType proxyType;
        try {
            proxyType = ProxyType.valueOf(proxyTypeStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            log.warn("Unsupported proxy type: {}", proxyTypeStr);
            return null;
        }
        Proxy proxy = new Proxy();
        proxy.setProxyType(proxyType);
        String proxyStr = driverOptions.get(PROXY);
        if (proxyStr != null) {
            switch (proxyType) {
            case MANUAL:
                proxy.setHttpProxy(proxyStr)
                    .setSslProxy(proxyStr);
                break;
            case PAC:
                proxy.setProxyAutoconfigUrl(proxyStr);
                break;
            default:
                log.warn("--proxy is not supported if proxy type is {}.", proxyTypeStr);
                break;
            }
        }
        if (driverOptions.has(NO_PROXY))
            proxy.setNoProxy(driverOptions.get(NO_PROXY));
        return proxy;
    }

    protected DesiredCapabilities setupProxy(DesiredCapabilities caps, DriverOptions driverOptions) {
        Proxy proxy = newProxy(driverOptions);
        if (proxy != null) {
            if (isProxySupported()) {
                caps.setCapability(CapabilityType.PROXY, proxy);
            } else {
                log.warn("No support proxy with {}. Please set proxy to browser configuration in advance.",
                    getClass().getSimpleName().replaceFirst("Factory$", ""));
            }
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
