package jp.vmi.selenium.webdriver;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import jp.vmi.selenium.selenese.utils.PathUtils;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link FirefoxDriver}.
 */
public class FirefoxDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "firefox";

    private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    @Override
    protected DesiredCapabilities setupProxy(DesiredCapabilities caps, DriverOptions driverOptions) {
        if (driverOptions.has(PROXY)) {
            String[] p = driverOptions.get(PROXY).split(":", 2);
            String proxy = p[0];
            int port = p.length == 2 ? Integer.parseInt(p[1]) : 80;
            JsonObject json = new JsonObject();
            json.addProperty("proxyType", "MANUAL");
            json.addProperty("httpProxy", proxy);
            json.addProperty("httpProxyPort", port);
            json.addProperty("sslProxy", proxy);
            json.addProperty("sslProxyPort", port);
            if (driverOptions.has(NO_PROXY)) {
                // don't work?
                json.addProperty("noProxy", driverOptions.get(NO_PROXY));
            }
            caps.setCapability("proxy", json);
        }
        return caps;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        if (driverOptions.has(GECKODRIVER)) {
            String executable = PathUtils.normalize(driverOptions.get(GECKODRIVER));
            if (!new File(executable).canExecute())
                throw new IllegalArgumentException("Missing GeckoDriver: " + executable);
            System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, executable);
        }
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        DesiredCapabilities requiredCaps = new DesiredCapabilities();
        setupProxy(requiredCaps, driverOptions);
        firefoxOptions.addRequiredCapabilities(requiredCaps);
        firefoxOptions.addDesiredCapabilities(driverOptions.getCapabilities());
        String firefoxBin = getFirefoxBinary(driverOptions);
        if (firefoxBin != null)
            firefoxOptions.setBinary(firefoxBin);
        if (driverOptions.has(CLI_ARGS))
            firefoxOptions.addArguments(driverOptions.getCliArgs());
        FirefoxProfile profile = getFirefoxProfile(driverOptions);
        if (profile != null)
            firefoxOptions.setProfile(profile);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }

    /**
     * set driver specific capabilities for RemoteWebDriver.
     *
     * @param caps desired capabilities.
     * @param driverOptions driver options.
     */
    public static void setDriverSpecificCapabilitiesForRemoteWebDriver(DesiredCapabilities caps, DriverOptions driverOptions) {
        String firefoxBin = getFirefoxBinary(driverOptions);
        if (firefoxBin != null) {
            caps.setCapability(FirefoxDriver.BINARY, firefoxBin);
            log.info("Firefox binary: {}", firefoxBin);
        }
        if (driverOptions.has(CLI_ARGS))
            log.warn("Ignore --cli-args with RemoteWebDriver.");
        FirefoxProfile profile = getFirefoxProfile(driverOptions);
        if (driverOptions.has(PROXY)) {
            if (profile == null)
                profile = new FirefoxProfile();
            setProxyConfiguration(profile, driverOptions);
        }
        if (profile != null) {
            String json;
            try {
                json = profile.toJson();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            caps.setCapability(FirefoxDriver.PROFILE, json);
            log.info("Convert Firefox profile to JSON: {} bytes", json.length());
        }
    }

    private static String getFirefoxBinary(DriverOptions driverOptions) {
        if (driverOptions.has(FIREFOX))
            return driverOptions.get(FIREFOX);
        else
            return System.getProperty(FirefoxDriver.SystemProperty.BROWSER_BINARY);
    }

    private static FirefoxProfile getFirefoxProfile(DriverOptions driverOptions) {
        if (!driverOptions.has(PROFILE) && !driverOptions.has(PROFILE_DIR))
            return null;
        FirefoxProfile profile;
        // Create FirefoxProfile and set to DesiredCapabilities.
        // (FirefoxProfile object can work with both local and remote FirefoxDriver
        //  see: https://code.google.com/p/selenium/wiki/DesiredCapabilities#Firefox_specific)
        String profileName = driverOptions.get(PROFILE);
        String profileDir = driverOptions.get(PROFILE_DIR);
        if (profileName != null) {
            if (profileDir != null)
                throw new IllegalArgumentException("Can't specify both '--profile' and '--profile-dir' at once");
            // see http://code.google.com/p/selenium/wiki/TipsAndTricks
            ProfilesIni allProfiles = new ProfilesIni();
            profile = allProfiles.getProfile(profileName);
            if (profile == null)
                throw new IllegalArgumentException("Profile '" + profile + "' does not exist.");
            log.info("Firefox profile: {}", profileName);
        } else {
            File dir = new File(profileDir);
            if (!dir.isDirectory())
                throw new IllegalArgumentException("Missing profile directory: " + profileDir);
            profile = new FirefoxProfile(dir);
            log.info("Firefox profile directory: {}", profileDir);
        }
        return profile;
    }

    private static void setProxyConfiguration(FirefoxProfile profile, DriverOptions driverOptions) {
        String proxy = driverOptions.get(PROXY);
        String[] ps = proxy.split(":", 2);
        String host = ps[0];
        int port = ps.length == 2 ? Integer.parseInt(ps[1]) : 80;
        String noProxy = driverOptions.has(NO_PROXY) ? driverOptions.get(NO_PROXY) : " ";
        // see https://developer.mozilla.org/en-US/docs/Mozilla/Preferences/Mozilla_networking_preferences#Proxy
        profile.setPreference("network.proxy.type", 1); // 1 = MANUAL
        profile.setPreference("network.proxy.http", host);
        profile.setPreference("network.proxy.http_port", port);
        profile.setPreference("network.proxy.ssl", host);
        profile.setPreference("network.proxy.ssl_port", port);
        profile.setPreference("network.proxy.ftp", host);
        profile.setPreference("network.proxy.ftp_port", port);
        profile.setPreference("network.proxy.socks", host);
        profile.setPreference("network.proxy.socks_port", port);
        profile.setPreference("network.proxy.no_proxies_on", noProxy);
    }
}
