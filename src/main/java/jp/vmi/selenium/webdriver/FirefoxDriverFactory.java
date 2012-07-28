package jp.vmi.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

public class FirefoxDriverFactory extends WebDriverFactory {

    public static final String WEBDRIVER_FIREFOX_BIN = "webdriver.firefox.bin";

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

        DesiredCapabilities capabilities = setupProxy(DesiredCapabilities.firefox(), driverOptions);

        return new FirefoxDriver(binary, profile, capabilities);
    }
}
