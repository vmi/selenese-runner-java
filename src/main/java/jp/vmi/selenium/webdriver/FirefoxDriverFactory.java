package jp.vmi.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.InvalidConfigurationException;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

public class FirefoxDriverFactory extends WebDriverFactory {

    private static Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

    private final String profileName;
    private final File profileDir;

    FirefoxDriverFactory(DriverOptions options) throws InvalidConfigurationException {
        super(options);
        profileName = options.get(PROFILE);
        String dir = options.get(PROFILE_DIR);
        profileDir = dir != null ? new File(dir) : null;
        if (profileName != null && profileDir != null)
            throw new InvalidConfigurationException("Can't designate '--profile' and '--profile-dir' at once");
        if (profileDir != null && !profileDir.isDirectory())
            throw new InvalidConfigurationException("Missing profile directory: " + profileDir);
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
        FirefoxBinary binary = new FirefoxBinary();
        WebDriver driver = new FirefoxDriver(binary, profile, capabilities);
        log.info("FirefoxDriver initialized.");
        return driver;
    }
}
