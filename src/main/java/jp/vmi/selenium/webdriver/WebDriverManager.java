package jp.vmi.selenium.webdriver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

public class WebDriverManager implements Supplier<WebDriver> {

    private static final Logger log = LoggerFactory.getLogger(WebDriverManager.class);

    public static final String FIREFOX = "firefox";

    public static final String CHROME = "chrome";

    public static final String IE = "ie";

    public static final String HTMLUNIT = "htmlunit";

    public static final String SAFARI = "safari";

    public static final String WEBDRIVER_FACTORY = "jp.vmi.selenium.webdriver.factory";

    private static final WebDriverManager manager = new WebDriverManager();

    private boolean isSingleInstance = true;

    private WebDriverFactory factory;

    private DriverOptions driverOptions = new DriverOptions();

    private final Map<String, WebDriver> driverMap = new HashMap<String, WebDriver>();

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

    public boolean isSingleInstance() {
        return isSingleInstance;
    }

    public void setSingleInstance(boolean isSingleInstance) {
        this.isSingleInstance = isSingleInstance;
    }

    public void setWebDriverFactory(WebDriverFactory factory) {
        this.factory = factory;
    }

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

    public DriverOptions getDriverOptions() {
        return driverOptions;
    }

    public void setDriverOptions(DriverOptions options) {
        this.driverOptions = options;
    }

    @Override
    public synchronized WebDriver get() throws IllegalArgumentException {
        String key = factory.getClass().getCanonicalName() + driverOptions.toString();
        WebDriver driver = driverMap.get(key);
        if (driver == null) {
            if (isSingleInstance)
                quitAllDrivers();
            driver = factory.newInstance(driverOptions);
            log.info("{} initialized.", driver.getClass().getSimpleName());
            driverMap.put(key, driver);
        }
        return driver;
    }

    public synchronized void quitAllDrivers() {
        for (WebDriver driver : driverMap.values()) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
                log.info("quit: " + driver.getClass().getSimpleName());
            }
        }
        driverMap.clear();
    }
}
