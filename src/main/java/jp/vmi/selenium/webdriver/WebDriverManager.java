package jp.vmi.selenium.webdriver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

/**
 * Manager of {@link WebDriver} instances.
 */
public class WebDriverManager implements Supplier<WebDriver> {

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
     * System property name for user defined {@link WebDriverFactory}.
     */
    public static final String WEBDRIVER_FACTORY = "jp.vmi.selenium.webdriver.factory";

    private static final WebDriverManager manager = new WebDriverManager();

    private boolean isSingleInstance = true;

    private WebDriverFactory factory;

    private DriverOptions driverOptions = new DriverOptions();

    private final Map<String, WebDriver> driverMap = new HashMap<String, WebDriver>();

    private final Map<String, String> environmentVariables = new HashMap<String, String>();

    /**
     * Get WebDriverManager instance. (singleton)
     *
     * @return WebDriverMangaer.
     */
    public static WebDriverManager getInstance() {
        return manager;
    }

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
        else {
            try {
                factory = (WebDriverFactory) Class.forName(factoryName).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid WebDriverFactory class name: " + factoryName, e);
            }
        }
        setWebDriverFactory(factory);
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

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    @Override
    public synchronized WebDriver get() throws IllegalArgumentException {
        factory.getEnvironmentVariables().clear();
        factory.getEnvironmentVariables().putAll(getEnvironmentVariables());

        String key = factory.getClass().getCanonicalName() + driverOptions.toString();
        WebDriver driver = driverMap.get(key);
        if (driver == null) {
            if (isSingleInstance)
                quitAllDrivers();
            driver = factory.newInstance(driverOptions);
            log.info("Initialized: {}", getDriverName(driver));
            driverMap.put(key, driver);
        }
        return driver;
    }

    /**
     * Quit all WebDriver instances.
     */
    public synchronized void quitAllDrivers() {
        for (WebDriver driver : driverMap.values()) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
                log.info("Quit: {}", getDriverName(driver));
            }
        }
        driverMap.clear();
    }
}
