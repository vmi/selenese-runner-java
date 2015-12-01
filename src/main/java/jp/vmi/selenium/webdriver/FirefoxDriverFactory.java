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
 * Factory of {@link FirefoxDriver}.
 */
public class FirefoxDriverFactory extends WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(FirefoxDriverFactory.class);

    /**
     * System property name for specifying Firefox binary.
     */
    public static final String WEBDRIVER_FIREFOX_BIN = "webdriver.firefox.bin";

    /**
     * set driver specific capabilities.
     *
     * @param caps desired capabilities.
     * @param driverOptions driver options.
     * @param isRemote set up for remote if true.
     */
    public static void setDriverSpecificCapabilities(DesiredCapabilities caps, DriverOptions driverOptions, boolean isRemote) {
        setFirefoxBinary(caps, driverOptions, isRemote);
        setFirefoxProfile(caps, driverOptions, isRemote);
    }

    private static void setFirefoxBinary(DesiredCapabilities caps, DriverOptions driverOptions, boolean isRemote) {
        // Set up firefox binary.
        // Validate "webdriver.firefox.bin" value bacause FirefoxBinary only ignore invalid it.
        String firefoxBin = System.getProperty(WEBDRIVER_FIREFOX_BIN);
        // Override by command line option.
        if (driverOptions.has(FIREFOX)) {
            firefoxBin = driverOptions.get(FIREFOX);
            System.setProperty(WEBDRIVER_FIREFOX_BIN, firefoxBin);
        }
        if (isRemote) {
            if (firefoxBin != null) {
                caps.setCapability(FirefoxDriver.BINARY, firefoxBin);
                log.info("Firefox binary: {}", firefoxBin);
            }
            if (driverOptions.has(CLI_ARGS))
                log.warn("Ignore --cli-args with RemoteWebDriver.");
            return;
        }
        FirefoxBinary binary;
        try {
            if (firefoxBin != null) {
                File file = new File(firefoxBin);
                if (!file.isFile() || !file.canExecute())
                    throw new IllegalArgumentException("Missing Firefox binary: " + firefoxBin);
                binary = new FirefoxBinary(file);
                log.info("Firefox binary: {}", firefoxBin);
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
    }

    private static void setFirefoxProfile(DesiredCapabilities caps, DriverOptions driverOptions, boolean isRemote) {
        if (driverOptions.has(PROFILE) || driverOptions.has(PROFILE_DIR)) {
            // Create FirefoxProfile and set to DesiredCapabilities.
            // (FirefoxProfile object can work with both local and remote FirefoxDriver
            //  see: https://code.google.com/p/selenium/wiki/DesiredCapabilities#Firefox_specific)
            String profileName = driverOptions.get(PROFILE);
            String profileDir = driverOptions.get(PROFILE_DIR);
            FirefoxProfile profile;
            if (profileName != null) {
                if (profileDir != null)
                    throw new IllegalArgumentException("Can't specify both '--profile' and '--profile-dir' at once");
                // see http://code.google.com/p/selenium/wiki/TipsAndTricks
                ProfilesIni allProfiles = new ProfilesIni();
                profile = allProfiles.getProfile(profileName);
                log.info("Firefox profile: {}", profileName);
            } else {
                File dir = new File(profileDir);
                if (!dir.isDirectory())
                    throw new IllegalArgumentException("Missing profile directory: " + profileDir);
                profile = new FirefoxProfile(dir);
                log.info("Firefox profile directory: {}", profileDir);
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
    }

    @Override
    public WebDriver newInstance(DriverOptions driverOptions) {
        DesiredCapabilities caps = DesiredCapabilities.firefox(); 
        FirefoxProfile fp = null;
        if (driverOptions.has(PROXY) && (!driverOptions.has(PROFILE)) && (!driverOptions.has(PROFILE_DIR))) {
        	fp = generateBlankProxyProfile(driverOptions);
        } else {
        	setupProxy(caps, driverOptions);
        }         
        caps.merge(driverOptions.getCapabilities());
        setDriverSpecificCapabilities(caps, driverOptions, false);
        FirefoxDriver driver = new FirefoxDriver(null, fp, caps);
        setInitialWindowSize(driver, driverOptions);
        return driver;
    }

	private FirefoxProfile generateBlankProxyProfile(DriverOptions driverOptions) {
		FirefoxProfile fp;
		fp = new FirefoxProfile();
		String ps = driverOptions.get(PROXY);
		fp.setPreference("network.proxy.type", 1);
		fp.setPreference("network.proxy.http", ps.split(":")[0]);
		fp.setPreference("network.proxy.http_port", ps.split(":")[1]);
		fp.setPreference("network.proxy.ssl", ps.split(":")[0]);
		fp.setPreference("network.proxy.ssl_port", ps.split(":")[1]);
		fp.setPreference("network.proxy.ftp", ps.split(":")[0]);
		fp.setPreference("network.proxy.ftp_port", ps.split(":")[1]);
		fp.setPreference("network.proxy.socks", ps.split(":")[0]);
		fp.setPreference("network.proxy.socks_port", ps.split(":")[1]);
		fp.setPreference("browser.startup.homepage", "about:blank");
		fp.setPreference("startup.homepage_welcome_url", "about:blank");
		fp.setPreference("startup.homepage_welcome_url.additional", "about:blank");
		return fp;
	}
}
