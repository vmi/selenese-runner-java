package jp.vmi.selenium.webdriver;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;

/**
 * Factory of {@link FirefoxDriver}.
 */
public class FirefoxDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

    /**
     * System property name for specifying Firefox binary.
     */
    public static final String WEBDRIVER_FIREFOX_BIN = "webdriver.firefox.bin";
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 768;

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        // Validate "webdriver.firefox.bin" value bacause FirefoxBinary only ignore invalid it.
        String path = System.getProperty(WEBDRIVER_FIREFOX_BIN);
        if (path != null) {
            File file = new File(path);
            if (!file.isFile() || !file.canExecute())
                throw new IllegalArgumentException("Executable file does not exist: " + path
                    + " defined by \"" + WEBDRIVER_FIREFOX_BIN + "\"");
        }
        FirefoxBinary binary;
        try {
            binary = new FirefoxBinary();
        } catch (WebDriverException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        for (Map.Entry<String, String> entry : getEnvironmentVariables().entrySet()) {
            binary.setEnvironmentProperty(entry.getKey(), entry.getValue());
        }

        String profileName = driverOptions.get(PROFILE);
        String dir = driverOptions.get(PROFILE_DIR);
        FirefoxProfile profile;
        if (profileName != null) {
            if (dir != null)
                throw new IllegalArgumentException("Can't specify both '--profile' and '--profile-dir' at once");
            // see http://code.google.com/p/selenium/wiki/TipsAndTricks
            ProfilesIni allProfiles = new ProfilesIni();
            profile = allProfiles.getProfile(profileName);
        } else if (dir != null) {
            File file = new File(dir);
            if (!file.isDirectory())
                throw new IllegalArgumentException("Missing profile directory: " + dir);
            profile = new FirefoxProfile(new File(dir));
        } else {
            profile = new FirefoxProfile();
        }



        DesiredCapabilities caps = setupProxy(DesiredCapabilities.firefox(), driverOptions);
        caps.merge(driverOptions.getCapabilities());
        FirefoxDriver driver = new FirefoxDriver(binary, profile, caps);
        int width = NumberUtils.toInt(driverOptions.get(DriverOption.WIDTH), DEFAULT_WIDTH);
        int height = NumberUtils.toInt(driverOptions.get(DriverOption.HEIGHT), DEFAULT_HEIGHT);
        driver.manage().window().setSize(new Dimension(width, height));
        log.info("firefox screen size: {}x{}", width, height);
        return driver;
    }
}
