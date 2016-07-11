package jp.vmi.selenium.webdriver;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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

/**
 * @author Heiko Does
 */
public class MarionetteDriverFactory extends WebDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(MarionetteDriverFactory.class);

    /**
     * System property name for specifying Firefox binary.
     */
    public static final String WEBDRIVER_GECKO_BIN = "webdriver.gecko.driver";

    /**
     * set driver specific capabilities.
     *
     * @param caps desired capabilities.
     * @param driverOptions driver options.
     * @param isRemote set up for remote if true.
     */
    public static void setDriverSpecificCapabilities(DesiredCapabilities caps, DriverOptions driverOptions, boolean isRemote) {
        setMarionetteBinary(caps, driverOptions, isRemote);
        setMarionetteProfile(caps, driverOptions, isRemote);
    }

    private static void setMarionetteBinary(DesiredCapabilities caps, DriverOptions driverOptions, boolean isRemote) {
        // Set up firefox binary.
        // Validate "webdriver.firefox.bin" value bacause FirefoxBinary only ignore invalid it.
        String marionetteBin = System.getProperty(WEBDRIVER_GECKO_BIN);
        // Override by command line option.
        if (driverOptions.has(MARIONETTE)) {
            marionetteBin = driverOptions.get(MARIONETTE);
            System.setProperty(WEBDRIVER_GECKO_BIN, marionetteBin);
        }
        if (isRemote) {
            if (marionetteBin != null) {
                caps.setCapability(FirefoxDriver.BINARY, marionetteBin);
                log.info("Marionette binary: {}", marionetteBin);
            }
            if (driverOptions.has(CLI_ARGS))
                log.warn("Ignore --cli-args with RemoteWebDriver.");
            return;
        }
        FirefoxBinary binary;
        try {
            if (marionetteBin != null) {
                File file = new File(marionetteBin);
                if (!file.isFile() || !file.canExecute())
                    throw new IllegalArgumentException("Missing Marionette binary: " + marionetteBin);
                binary = new FirefoxBinary(file);
                log.info("Marionette binary: {}", marionetteBin);
            } else {
                binary = new FirefoxBinary();
            }
        } catch (WebDriverException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        if (driverOptions.has(CLI_ARGS)) {
            String[] cliArgs = driverOptions.getCliArgs();
            binary.addCommandLineOptions(cliArgs);
            log.info("Command line arguments: [{}]", StringUtils.join(cliArgs, "] ["));
        }
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : driverOptions.getEnvVars().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            binary.setEnvironmentProperty(key, value);
            if (isFirst) {
                log.info("Envrionment variables:");
                isFirst = false;
            }
            log.info("- [{}]=[{}]", key, StringEscapeUtils.escapeJava(value));
        }
        caps.setCapability(FirefoxDriver.BINARY, binary);
        caps.setCapability("marionette", true);
    }

    private static void setMarionetteProfile(DesiredCapabilities caps, DriverOptions driverOptions, boolean isRemote) {
        FirefoxProfile profile;
        if (driverOptions.has(PROFILE) || driverOptions.has(PROFILE_DIR)) {
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
                log.info("Marionette profile: {}", profileName);
            } else {
                File dir = new File(profileDir);
                if (!dir.isDirectory())
                    throw new IllegalArgumentException("Missing profile directory: " + profileDir);
                profile = new FirefoxProfile(dir);
                log.info("Marionette profile directory: {}", profileDir);
            }
        } else {
            profile = new FirefoxProfile();
            profile.setPreference("browser.startup.homepage", "about:blank");
            profile.setPreference("startup.homepage_welcome_url", "about:blank");
            profile.setPreference("startup.homepage_welcome_url.additional", "about:blank");
        }
        if (driverOptions.has(PROXY)) {
            String[] pss = driverOptions.get(PROXY).split(":");
            if (pss.length == 2) {
                String host = pss[0];
                int port = Integer.parseInt(pss[1]);
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
            } else {
                log.warn("Invalid proxy format (\"HOST:PORT\" required): {}", driverOptions.get(PROXY));
            }
        }
        if (isRemote) {
            String json;
            try {
                json = profile.toJson();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            caps.setCapability(FirefoxDriver.PROFILE, json);
            log.info("Convert Firefox profile to JSON: {} bytes", json.length());
        } else {
            caps.setCapability(FirefoxDriver.PROFILE, profile);
        }
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = DesiredCapabilities.firefox();
        setupProxy(caps, driverOptions);
        caps.merge(driverOptions.getCapabilities());
        setDriverSpecificCapabilities(caps, driverOptions, false);
        FirefoxDriver driver = new FirefoxDriver(caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }

}
