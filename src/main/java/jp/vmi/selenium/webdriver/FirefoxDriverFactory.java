package jp.vmi.selenium.webdriver;

import java.io.File;

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

public class FirefoxDriverFactory extends WebDriverFactory {

    private static Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

    private final String profileName;
    private final File profileDir;

    public FirefoxDriverFactory(DriverOptions options) throws IllegalArgumentException {
        super(options);
        profileName = options.get(PROFILE);
        String dir = options.get(PROFILE_DIR);
        profileDir = dir != null ? new File(dir) : null;
        if (profileName != null && profileDir != null)
            throw new IllegalArgumentException("Can't designate '--profile' and '--profile-dir' at once");
        if (profileDir != null && !profileDir.isDirectory())
            throw new IllegalArgumentException("Missing profile directory: " + profileDir);
    }

    @Override
    protected DesiredCapabilities defaultCapabilities() {
        return DesiredCapabilities.firefox();
    }

    @Override
    public WebDriver initDriver() {
        FirefoxProfile profile;
        if (profileName != null) {
            // see http://code.google.com/p/selenium/wiki/TipsAndTricks
            ProfilesIni allProfiles = new ProfilesIni();
            profile = allProfiles.getProfile(profileName);
        } else {
            profile = new FirefoxProfile(profileDir);
        }
        String path = System.getProperty("webdriver.firefox.bin");
        WebDriver driver = null;
        try {
            FirefoxBinary binary = null;
            if (path == null) {
                binary = new FirefoxBinary();
            } else {
                binary = new FirefoxBinary(new File(path));
            }

            driver = new FirefoxDriver(binary, profile, capabilities);

        } catch (NullPointerException e) {
            // firefox not found in webdriver.firefox.bin systemproperty.
            throw new BrowserNotFoundException(e.getMessage());
        } catch (WebDriverException e) {
            // firefox not found.
            throw new BrowserNotFoundException(e.getMessage());
        }
        log.info("FirefoxDriver initialized.");
        return driver;
    }
}
