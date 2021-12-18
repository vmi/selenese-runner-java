package jp.vmi.selenium.webdriver;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.utils.LangUtils;

/**
 * Manager of {@link WebDriver} instances.
 */
public class WebDriverManager implements WebDriverPreparator {

    private static final Logger log = LoggerFactory.getLogger(WebDriverManager.class);

    /**
     * Firefox.
     */
    public static final String FIREFOX = FirefoxDriverFactory.BROWSER_NAME;

    /**
     * Chrome.
     */
    public static final String CHROME = ChromeDriverFactory.BROWSER_NAME;

    /**
     * Internet Explorer.
     */
    public static final String IE = IEDriverFactory.BROWSER_NAME;

    /**
     * Edge.
     */
    public static final String EDGE = EdgeDriverFactory.BROWSER_NAME;

    /**
     * Html Unit.
     */
    public static final String HTMLUNIT = HtmlUnitDriverFactory.BROWSER_NAME;

    /**
     * Safari.
     */
    public static final String SAFARI = SafariDriverFactory.BROWSER_NAME;

    /**
     *  RemoteWebDriver
     */
    public static final String REMOTE = RemoteWebDriverFactory.BROWSER_NAME;

    /**
     *  AppiumWebDriver
     */
    public static final String APPIUM = AppiumWebDriverFactory.BROWSER_NAME;

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
        if (LangUtils.isBlank(name))
            return driver.getClass().getName();
        else
            return name;
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

    @Override
    public String getBrowserName() {
        return factory.getBrowserName();
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
        if (LangUtils.isBlank(factoryName))
            factoryName = FIREFOX;
        switch (factoryName.toLowerCase()) {
        case FIREFOX:
            return new FirefoxDriverFactory();
        case CHROME:
            return new ChromeDriverFactory();
        case IE:
            return new IEDriverFactory();
        case EDGE:
            return new EdgeDriverFactory();
        case SAFARI:
            return new SafariDriverFactory();
        case HTMLUNIT:
            return new HtmlUnitDriverFactory();
        case REMOTE:
            return new RemoteWebDriverFactory();
        case APPIUM:
            return new AppiumWebDriverFactory();
        default:
            try {
                return (WebDriverFactory) Class.forName(factoryName).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid WebDriverFactory class name: " + factoryName, e);
            }
        }
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
        String msg = t.getMessage();
        if (msg != null && msg.startsWith("browser is undefined"))
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
                log.info("Existing driver found: {}", getDriverName(driver));
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
