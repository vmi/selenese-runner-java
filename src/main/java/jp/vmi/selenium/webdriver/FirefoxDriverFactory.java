package jp.vmi.selenium.webdriver;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link FirefoxDriver}.
 */
public class FirefoxDriverFactory extends WebDriverFactory {

    /**
     * System property name for specifying Firefox binary.
     */
    public static final String WEBDRIVER_FIREFOX_BIN = "webdriver.firefox.bin";

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        // Validate "webdriver.firefox.bin" value bacause FirefoxBinary only ignore invalid it.
        String path = System.getProperty(WEBDRIVER_FIREFOX_BIN);
        // Override by command line option.
        if (driverOptions.has(FIREFOX)) {
            path = driverOptions.get(FIREFOX);
            System.setProperty(WEBDRIVER_FIREFOX_BIN, path);
        }
        if (path != null) {
            File file = new File(path);
            if (!file.isFile() || !file.canExecute())
                throw new IllegalArgumentException("Missing Firefox binary: " + path);
        }
        FirefoxBinary binary;
        try {
            binary = new FirefoxBinary();
        } catch (WebDriverException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        if (driverOptions.has(CLI_ARGS))
            binary.addCommandLineOptions(driverOptions.getCliArgs());
        for (Map.Entry<String, String> entry : driverOptions.getEnvVars().entrySet())
            binary.setEnvironmentProperty(entry.getKey(), entry.getValue());

        FirefoxProfile profile = (FirefoxProfile) driverOptions.getCapabilities().getCapability(FirefoxDriver.PROFILE);
        if (profile == null)
            profile = new FirefoxProfile();

        DesiredCapabilities caps = setupProxy(DesiredCapabilities.firefox(), driverOptions);
        caps.merge(driverOptions.getCapabilities());
        FirefoxDriver driver = new FirefoxDriver(binary, profile, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }
}
