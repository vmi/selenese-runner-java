package jp.vmi.selenium.testutils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;

import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.FirefoxDriverFactory;
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

    public static void setFirefoxBinary(Object factory, DriverOptions driverOptions) {
        if (factory.equals(WebDriverManager.FIREFOX) || factory instanceof FirefoxDriverFactory) {
            Path conf = Paths.get("tmp/firefox-bin.conf");
            if (Files.exists(conf)) {
                try {
                    List<String> lines = Files.readAllLines(conf, StandardCharsets.UTF_8);
                    if (!lines.isEmpty()) {
                        for (String line : lines) {
                            if (!line.startsWith("#")) {
                                driverOptions.set(DriverOption.FIREFOX, line);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    // no operation.
                }
            }
        }
    }

    public static void setWebDriverFactory(String factoryName, DriverOptions driverOptions) {
        try {
            setFirefoxBinary(factoryName, driverOptions);
            manager.setWebDriverFactory(factoryName);
            manager.setDriverOptions(driverOptions);
            TestBase.factoryName = factoryName;
        } catch (UnsupportedOperationException e) {
            Assume.assumeNoException(e);
        }
    }
}
