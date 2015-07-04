package jp.vmi.selenium.webdriver;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Manager of {@link WebDriver} instances.
 */
public class WebDriverManager implements WebDriverPreparator {

    private static final Logger log = LoggerFactory.getLogger(WebDriverManager.class);

    /**
     * Firefox.
     */
    public static final String FIREFOX = "firefox";

    /**
     * Chrome.
     */
    public static final String CHROME = "chrome";

    /**
     * Internet Explorer.
     */
    public static final String IE = "ie";

    /**
     * Html Unit.
     */
    public static final String HTMLUNIT = "htmlunit";

    /**
     * Safari.
     */
    public static final String SAFARI = "safari";

    /**
     *  RemoteWebDriver
     */
    public static final String REMOTE = "remote";

    /**
     *  AppiumWebDriver
     */
    public static final String APPIUM = "appium";

    /**
     * PhantomJS
     */
    public static final String PHANTOMJS = "phantomjs";

    /**
     * AndroidDriver
     */
    @Deprecated
    public static final String ANDROID = "android";

    private static class Builder {

        private final WebDriverFactory factory;
        private final DriverOptions driverOptions;

        public Builder(WebDriverFactory factory, DriverOptions driverOptions) {
            this.factory = factory;
            this.driverOptions = new DriverOptions(driverOptions);
        }

        public boolean isParameterChanged(WebDriverFactory factory, DriverOptions driverOptions) {
            return !this.factory.equals(factory) || !this.driverOptions.toString().equals(driverOptions.toString());
        }

        public WebDriver build() {
            return factory.newInstance(driverOptions);
        }

    }

    /**
     * System property name for user defined {@link WebDriverFactory}.
     */
    public static final String WEBDRIVER_FACTORY = "jp.vmi.selenium.webdriver.factory";

    // for backward compatibility.
    private static WebDriverManager manager = null;

    private static final Set<WebDriverManager> managers = Collections.newSetFromMap(new WeakHashMap<WebDriverManager, Boolean>());

    private WebDriverFactory factory;

    private DriverOptions driverOptions = new DriverOptions();

    private Builder builder = null;

    private WebDriver driver = null;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                quitDriversOnAllManagers();
            }
        });
    }

    private static String getDriverName(WebDriver driver) {
        String name = driver.getClass().getSimpleName();
        if (StringUtils.isNotBlank(name))
            return name;
        else
            return driver.getClass().getName();
    }

    /**
     * Get WebDriverManager instance. (singleton)
     *
     * @deprecated Use {@link #newInstance()} instead of this.
     *
     * @return WebDriverMangaer.
     */
    @Deprecated
    public static synchronized WebDriverManager getInstance() {
        if (manager == null)
            manager = newInstance();
        return manager;
    }

    /**
     * Construct WebDriverManager instance.
     *
     * @return WebDriverMangaer.
     */
    public static WebDriverManager newInstance() {
        WebDriverManager manager = new WebDriverManager();
        synchronized (managers) {
            managers.add(manager);
        }
        return manager;
    }

    /**
     * Constructor.
     */
    private WebDriverManager() {
        setWebDriverFactory(System.getProperty(WEBDRIVER_FACTORY, FIREFOX));
    }

    /**
     * Is single instance of WebDriver?
     *
     * @deprecated This does not work. Please just remove it.
     *
     * @return true if the number of WebDriver instance is only 1.
     */
    @Deprecated
    public boolean isSingleInstance() {
        return true;
    }

    /**
     * Set single instance of WebDriver.
     *
     * @deprecated This does not work. Please just remove it.
     *
     * @param isSingleInstance if true, the number of WebDriver instance is only 1.
     */
    @Deprecated
    public void setSingleInstance(boolean isSingleInstance) {
        // no operation.
    }

    /**
     * Set current WebDriverFactory.
     *
     * @param factory WebDriverFactory instance.
     */
    public void setWebDriverFactory(WebDriverFactory factory) {
        this.factory = factory;
    }

    /**
     * Set current WebDriverFactory name.
     *
     * @param factoryName WebDriverFactory name.
     */
    public void setWebDriverFactory(String factoryName) {
        WebDriverFactory factory = lookupWebDriverFactory(factoryName);
        setWebDriverFactory(factory);
    }

    /**
     * Lookup WebDriverFactory by name.
     *
     * @param factoryName WebDriverFactory name.
     *
     * @return WebDriverFactory instance.
     */
    public WebDriverFactory lookupWebDriverFactory(String factoryName) {
        if (StringUtils.isBlank(factoryName))
            factoryName = FIREFOX;
        else
            factoryName = factoryName.toLowerCase();
        WebDriverFactory factory;
        if (FIREFOX.equals(factoryName)) {
            factory = new FirefoxDriverFactory();
        } else if (CHROME.equals(factoryName)) {
            factory = new ChromeDriverFactory();
        } else if (IE.equals(factoryName)) {
            factory = new IEDriverFactory();
        } else if (SAFARI.equals(factoryName)) {
            factory = new SafariDriverFactory();
        } else if (HTMLUNIT.equals(factoryName)) {
            factory = new HtmlUnitDriverFactory();
        } else if (REMOTE.equals(factoryName)) {
            factory = new RemoteWebDriverFactory();
        } else if (APPIUM.equals(factoryName)) {
            factory = new AppiumWebDriverFactory();
        } else if (PHANTOMJS.equals(factoryName)) {
            factory = new PhantomJSDriverFactory();
        } else {
            try {
                factory = (WebDriverFactory) Class.forName(factoryName).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid WebDriverFactory class name: " + factoryName, e);
            }
        }
        return factory;
    }

    /**
     * Get current driver options.
     *
     * @return DriverOptions instance.
     */
    public DriverOptions getDriverOptions() {
        return driverOptions;
    }

    /**
     * Set current driver options.
     *
     * @param driverOptions DriverOptions instance.
     */
    public void setDriverOptions(DriverOptions driverOptions) {
        this.driverOptions = driverOptions;
    }

    private boolean isBrowserUnreachable(Throwable t) {
        if (t instanceof UnreachableBrowserException)
            return true;
        while (t instanceof SeleniumException)
            t = t.getCause();
        if (t instanceof WebDriverException && StringUtils.contains(t.getMessage(), "not reachable"))
            return true;
        return false;
    }

    private boolean isDriverReusable() {
        if (driver == null)
            return false;
        try {
            driver.getWindowHandle();
            // ChromeDriver does not send UnreachableBrowserException.
            // TODO fix me when ChromeDriver is fixed.
            if (driver instanceof ChromeDriver)
                driver.getTitle();
            return true;
        } catch (NoSuchWindowException e) {
            log.info("No focused window.");
            Set<String> handles = driver.getWindowHandles();
            if (!handles.isEmpty()) {
                log.info("Activate a window.");
                driver.switchTo().window(handles.iterator().next());
                return true;
            }
            log.warn("No window exists.");
        } catch (RuntimeException e) {
            if (!isBrowserUnreachable(e))
                throw e;
            log.warn("Browser might crash.");
        }
        try {
            driver.quit();
        } catch (Throwable e) {
            // no operation
        }
        return false;
    }

    @Override
    public synchronized WebDriver get() throws IllegalArgumentException {
        if (builder == null || builder.isParameterChanged(factory, driverOptions)) {
            builder = new Builder(factory, driverOptions);
        } else {
            if (isDriverReusable()) {
                log.info("Existing driver found.");
                return driver;
            } else {
                log.info("Restart driver.");
            }
        }
        quitDriver();
        driver = builder.build();
        log.info("Initialized: {}", getDriverName(driver));
        return driver;
    }

    /**
     * Quit WebDriver instance.
     */
    public synchronized void quitDriver() {
        if (driver == null)
            return;
        try {
            driver.quit();
        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            log.info("Quit: {}", getDriverName(driver));
        }
        driver = null;
    }

    /**
     * Quit all WebDriver instances.
     *
     * @deprecated use {@link #quitDriver()} instead of this.
     */
    @Deprecated
    public void quitAllDrivers() {
        quitDriver();
    }

    @Override
    protected void finalize() throws Throwable {
        quitDriver();
    }

    /**
     * Quit WebDriver instances on all WebDriverManager instances.
     */
    public static synchronized void quitDriversOnAllManagers() {
        synchronized (managers) {
            for (WebDriverManager manager : managers)
                manager.quitDriver();
        }
    }
}
