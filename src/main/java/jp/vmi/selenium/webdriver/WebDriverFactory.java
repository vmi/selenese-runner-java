package jp.vmi.selenium.webdriver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

abstract public class WebDriverFactory implements Supplier<WebDriver> {

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    public static final String WEBDRIVER_FACTORY = "jp.vmi.selenium.webdriver.factory";

    private static final String FACTORY_CLASS_NAME_TMPL = WebDriverFactory.class.getPackage().getName() + ".%sDriverFactory";

    private static final Map<String, WebDriverFactory> factories = new HashMap<String, WebDriverFactory>();

    protected final DesiredCapabilities capabilities = defaultCapabilities();

    private WebDriver driver = null;

    public static synchronized WebDriverFactory getFactory(Class<? extends WebDriverFactory> clazz, DriverOptions options)
        throws IllegalArgumentException {
        WebDriverFactory factory = factories.get(clazz.getName());
        if (factory == null) {
            try {
                factory = clazz.getDeclaredConstructor(DriverOptions.class).newInstance(options);
                factories.put(clazz.getName(), factory);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException)
                    throw (IllegalArgumentException) e;
                else
                    throw new RuntimeException(e);
            }
        }
        return factory;
    }

    public static WebDriverFactory getFactory(String factoryClassName, DriverOptions options)
        throws ClassNotFoundException, IllegalArgumentException {
        if (StringUtils.isBlank(factoryClassName))
            throw new ClassNotFoundException("Missing factory class name");
        if (!factoryClassName.contains("."))
            factoryClassName = String.format(FACTORY_CLASS_NAME_TMPL, factoryClassName);
        return getFactory(Class.forName(factoryClassName).asSubclass(WebDriverFactory.class), options);
    }

    WebDriverFactory(DriverOptions options) throws IllegalArgumentException {
        if (options.has(PROXY)) {
            Proxy proxy = new Proxy();
            proxy.setProxyType(ProxyType.MANUAL);
            String ps = options.get(PROXY);
            proxy.setHttpProxy(ps)
                .setSslProxy(ps)
                .setFtpProxy(ps);
            if (options.has(NO_PROXY))
                proxy.setNoProxy(options.get(NO_PROXY));
            capabilities.setCapability(CapabilityType.PROXY, proxy);
        }
    }

    abstract protected DesiredCapabilities defaultCapabilities();

    @Override
    synchronized public WebDriver get() {
        if (driver == null) {
            try {
                driver = initDriver();
            } catch (RuntimeException e) {
                log.error(e.getMessage());
                throw e;
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        // TODO hide exception.
                        driver.quit();
                    } catch (NullPointerException e) {
                        log.warn("thrown NullPointerException");
                    }
                    driver = null;
                }
            });
        }
        return driver;
    }

    abstract protected WebDriver initDriver();
}
