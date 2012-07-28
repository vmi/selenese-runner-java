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

    public static final String WEBDRIVER_FACTORY = "jp.vmi.selenium.webdriver.factory";

    private static final WebDriverManager manager = new WebDriverManager();

    private boolean isSingleInstance = true;

    private WebDriverFactory factory = new FirefoxDriverFactory();

    private DriverOptions driverOptions = new DriverOptions();

    private final Map<String, WebDriver> driverMap = new HashMap<String, WebDriver>();

    public static WebDriverManager getInstance() {
        return manager;
    }

    private WebDriverManager() {
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
            factoryName = "firefox";
        else
            factoryName = factoryName.toLowerCase();
        WebDriverFactory factory;
        if ("firefox".equals(factoryName))
            factory = new FirefoxDriverFactory();
        else if ("chrome".equals(factoryName))
            factory = new ChromeDriverFactory();
        else if ("ie".equals(factoryName))
            factory = new IEDriverFactory();
        else if ("safari".equals(factoryName))
            factory = new SafariDriverFactory();
        else if ("htmlunit".equals(factoryName))
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
                log.info("quit driver: " + driver.getClass().getSimpleName());
            }
        }
        driverMap.clear();
    }
}
