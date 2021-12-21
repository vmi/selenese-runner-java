package jp.vmi.selenium.webdriver;

import java.io.File;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jp.vmi.selenium.webdriver.DriverOptions.DriverOption.*;

/**
 * Factory of {@link FirefoxDriver}.
 */
public class FirefoxDriverFactory extends WebDriverFactory {

    @SuppressWarnings("javadoc")
    public static final String BROWSER_NAME = "firefox";

    private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

    // proxy type
    // https://developer.mozilla.org/en-US/docs/Mozilla/Preferences/Mozilla_networking_preferences#Proxy
    private static final int MANUAL = 1;
    private static final int PAC = 2;
    private static final int AUTODETECT = 4; // == WPAD?

    @Override
    public String getBrowserName() {
        return BROWSER_NAME;
    }

    /**
     * Create and initialize FirefoxOptions.
     *
     * @param driverOptions driver options.
     * @return FirefoxOptions.
     */
    public static FirefoxOptions newFirefoxOptions(DriverOptions driverOptions) {
        FirefoxOptions options = new FirefoxOptions();
        if (driverOptions.has(HEADLESS))
            options.setHeadless(driverOptions.getBoolean(HEADLESS));
        Proxy proxy = newProxy(driverOptions);
        if (proxy != null)
            options.setProxy(proxy);
        return options;
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        GeckoDriverService service = setupBuilder(new GeckoDriverService.Builder(), driverOptions, GECKODRIVER).build();
        FirefoxOptions firefoxOptions = newFirefoxOptions(driverOptions);
        String firefoxBin = getFirefoxBinary(driverOptions);
        if (firefoxBin != null)
            firefoxOptions.setBinary(firefoxBin);
        if (driverOptions.has(CLI_ARGS))
            firefoxOptions.addArguments(driverOptions.getCliArgs());
        FirefoxProfile profile = getFirefoxProfile(driverOptions);
        if (profile != null)
            firefoxOptions.setProfile(profile);
        firefoxOptions.merge(driverOptions.getCapabilities());
        FirefoxDriver driver = new FirefoxDriver(service, firefoxOptions);
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
            caps.setCapability(FirefoxDriver.Capability.BINARY, firefoxBin);
            log.info("Firefox binary: {}", firefoxBin);
        }
        if (driverOptions.has(CLI_ARGS))
            log.warn("Ignore --cli-args with RemoteWebDriver.");
        FirefoxProfile profile = getFirefoxProfile(driverOptions);
        if (driverOptions.has(PROXY_TYPE) || driverOptions.has(PROXY)) {
            if (profile == null)
                profile = new FirefoxProfile();
            setProxyConfiguration(profile, driverOptions);
        }
        if (profile != null) {
            caps.setCapability(FirefoxDriver.Capability.PROFILE, profile);
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
        String proxyTypeStr = driverOptions.get(PROXY_TYPE);
        if (proxyTypeStr == null)
            proxyTypeStr = "manual";
        int proxyType;
        switch (proxyTypeStr) {
        case "manual":
            proxyType = MANUAL;
            break;
        case "pac":
            proxyType = PAC;
            break;
        case "autodetect":
            proxyType = AUTODETECT;
            break;
        default:
            log.warn("Unsupported proxy type: {}", proxyTypeStr);
            return;
        }
        String proxy = driverOptions.get(PROXY);
        String noProxy = driverOptions.has(NO_PROXY) ? driverOptions.get(NO_PROXY) : " ";
        // see https://developer.mozilla.org/en-US/docs/Mozilla/Preferences/Mozilla_networking_preferences#Proxy
        profile.setPreference("network.proxy.type", proxyType);
        if (proxy != null) {
            switch (proxyType) {
            case MANUAL:
                String[] ps = proxy.split(":", 2);
                String host = ps[0];
                int port = ps.length == 2 ? Integer.parseInt(ps[1]) : 80;
                profile.setPreference("network.proxy.http", host);
                profile.setPreference("network.proxy.http_port", port);
                profile.setPreference("network.proxy.ssl", host);
                profile.setPreference("network.proxy.ssl_port", port);
                profile.setPreference("network.proxy.socks", host);
                profile.setPreference("network.proxy.socks_port", port);
                break;

            case PAC:
                profile.setPreference("network.proxy.autoconfig_url", proxy);
                break;

            default:
                log.warn("--proxy is not supported if proxy type is {}.", proxyTypeStr);
                break;
            }
        }
        profile.setPreference("network.proxy.no_proxies_on", noProxy);
    }
}
