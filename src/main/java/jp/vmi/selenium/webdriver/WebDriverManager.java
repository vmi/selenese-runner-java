package jp.vmi.selenium.webdriver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
        public WebDriver driver;

        public Builder(WebDriverFactory factory, DriverOptions driverOptions) {
            this.factory = factory;
            this.driverOptions = driverOptions;
        }

        public String getKey() {
            return factory.getClass().getCanonicalName() + driverOptions.toString();
        }

        public Builder build() {
            driver = factory.newInstance(driverOptions);
            return this;
        }
    }

    /**
     * System property name for user defined {@link WebDriverFactory}.
     */
    public static final String WEBDRIVER_FACTORY = "jp.vmi.selenium.webdriver.factory";

    private static final WebDriverManager manager = new WebDriverManager();

    private boolean isSingleInstance = true;

    private WebDriverFactory factory;

    private DriverOptions driverOptions = new DriverOptions();

    private final Map<String, Builder> driverMap = new HashMap<String, Builder>();

    @Deprecated
    private final Map<String, String> environmentVariables = new HashMap<String, String>();

    /**
     * Get WebDriverManager instance. (singleton)
     *
     * @return WebDriverMangaer.
     */
    public static WebDriverManager getInstance() {
        return manager;
    }

    /**
     * Constructor.
     */
    private WebDriverManager() {
        String factoryName = System.getProperty(WEBDRIVER_FACTORY, FIREFOX);
        setWebDriverFactory(factoryName);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                quitAllDrivers();
            }
        });
    }

    /**
     * Is single instance of WebDriver?
     *
     * @return true if
     */
    public boolean isSingleInstance() {
        return isSingleInstance;
    }

    /**
     * Set single instance of WebDriver.
     *
     * @param isSingleInstance if true, the number of WebDriver instance is only 1.
     */
    public void setSingleInstance(boolean isSingleInstance) {
        this.isSingleInstance = isSingleInstance;
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
        if (FIREFOX.equals(factoryName))
            factory = new FirefoxDriverFactory();
        else if (CHROME.equals(factoryName))
            factory = new ChromeDriverFactory();
        else if (IE.equals(factoryName))
            factory = new IEDriverFactory();
        else if (SAFARI.equals(factoryName))
            factory = new SafariDriverFactory();
        else if (HTMLUNIT.equals(factoryName))
            factory = new HtmlUnitDriverFactory();
        else if (REMOTE.equals(factoryName)) {
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

    private String getDriverName(WebDriver driver) {
        String name = driver.getClass().getSimpleName();
        if (StringUtils.isNotBlank(name))
            return name;
        else
            return driver.getClass().getName();
    }

    /**
     * Get environment variable map.
     *
     * @return environment variable map.
     *
     * @deprecated use {@link DriverOptions#getEnvVars()}.
     */
    @Deprecated
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
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

    private boolean reprepare(WebDriver driver, String message) {
        try {
            driver.getWindowHandle();
            // ChromeDriver does not send UnreachableBrowserException.
            // TODO fix me when ChromeDriver is fixed.
            if (driver instanceof ChromeDriver)
                driver.getTitle();
            if (message != null)
                log.info(message);
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

    private synchronized WebDriver get(Builder builder) throws IllegalArgumentException {
        String key = builder.getKey();
        Builder prevBuilder = driverMap.get(key);
        if (prevBuilder != null) {
            WebDriver driver = prevBuilder.driver;
            if (reprepare(driver, "Existing driver found."))
                return driver;
        }
        if (isSingleInstance)
            quitAllDrivers();
        driverMap.put(builder.getKey(), builder.build());
        log.info("Initialized: {}", getDriverName(builder.driver));
        return builder.driver;
    }

    @Override
    public WebDriver get() throws IllegalArgumentException {
        DriverOptions dopts = new DriverOptions(driverOptions);
        dopts.getEnvVars().putAll(environmentVariables); // for backward compaitibility.
        return get(new Builder(factory, dopts));
    }

    @Override
    public WebDriver reprepare(WebDriver driver) {
        if (reprepare(driver, null))
            return driver;
        for (Entry<String, Builder> entry : driverMap.entrySet()) {
            Builder builder = entry.getValue();
            if (driver == builder.driver) {
                log.info("Restart driver.");
                return builder.build().driver;
            }
        }
        throw new IllegalArgumentException("The driver is not managed by WebDriverManager: " + driver);
    }

    /**
     * Quit all WebDriver instances.
     */
    public synchronized void quitAllDrivers() {
        for (Builder builder : driverMap.values()) {
            try {
                builder.driver.quit();
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
                log.info("Quit: {}", getDriverName(builder.driver));
            }
        }
        driverMap.clear();
    }
}
