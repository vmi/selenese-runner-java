package jp.vmi.selenium.testutils;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Base class for test.
 */
@SuppressWarnings("javadoc")
public class TestBase {

    /**
     * Webserver for test
     */
    @Rule
    public final WebServerResouce wsr = new WebServerResouce();

    protected static WebDriverManager manager = null;
    protected static String factoryName = null;

    @BeforeClass
    public static void baseSetup() {
        manager = WebDriverManager.newInstance();
    }

    @AfterClass
    public static void baseTeardown() {
        manager.quitDriver();
        manager = null;
        factoryName = null;
    }

    public static void setWebDriverFactory(String factoryName, DriverOptions driverOptions) {
        try {
            manager.setWebDriverFactory(factoryName);
            manager.setDriverOptions(driverOptions);
            TestBase.factoryName = factoryName;
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        }
    }
}
