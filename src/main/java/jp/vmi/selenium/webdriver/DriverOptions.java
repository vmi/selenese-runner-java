package jp.vmi.selenium.webdriver;

import java.util.IdentityHashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Options for WebDriver.
 */
public class DriverOptions {

    private static final Logger log = LoggerFactory.getLogger(DriverOptions.class);

    /**
     * WebDriver option.
     */
    public enum DriverOption {
        /** --profile */
        PROFILE,
        /** --profile-dir */
        PROFILE_DIR,
        /** --proxy */
        PROXY,
        /** --proxy-user */
        PROXY_USER,
        /** --proxy-password */
        PROXY_PASSWORD,
        /** --no-proxy */
        NO_PROXY,
        /** --chromedriver */
        CHROMEDRIVER,
        /** --iedriver */
        IEDRIVER,
        /** --phantomjs */
        PHANTOMJS,
        /** --remote-platform */
        REMOTE_PLATFORM,
        /** --remote-browser */
        REMOTE_BROWSER,
        /** --remote-version */
        REMOTE_VERSION,
        /** --remote-url */
        REMOTE_URL,
        /** --width */
        WIDTH,
        /** --height */
        HEIGHT,
    }

    private final IdentityHashMap<DriverOptions.DriverOption, String> map = Maps.newIdentityHashMap();
    private final String[] capDefs;

    /**
     * Constructs empty options.
     */
    public DriverOptions() {
        capDefs = ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * Constructs driver options specified by command line.
     *
     * @param cli parsed command line information.
     */
    public DriverOptions(CommandLine cli) {
        for (DriverOption opt : DriverOption.values()) {
            String key = opt.name().toLowerCase().replace('_', '-');
            set(opt, cli.getOptionValue(key));
        }
        if (cli.hasOption("define"))
            capDefs = cli.getOptionValues("define");
        else
            capDefs = ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * Get option value.
     *
     * @param opt option key.
     * @return option value.
     */
    public String get(DriverOption opt) {
        return map.get(opt);
    }

    /**
     * DriverOptions instance has specified option.
     *
     * @param opt option key.
     * @return true if has specified option.
     */
    public boolean has(DriverOption opt) {
        return map.containsKey(opt);
    }

    /**
     * Set option key and value.
     *
     * @param opt option key.
     * @param value option value.
     * @return this.
     */
    public DriverOptions set(DriverOption opt, String value) {
        if (value != null)
            map.put(opt, value);
        else
            map.remove(opt);
        return this;
    }

    @Override
    public String toString() {
        if (map.isEmpty())
            return "[]";
        StringBuilder result = new StringBuilder("[");
        for (DriverOption opt : DriverOption.values())
            if (map.containsKey(opt))
                result.append(opt.name()).append('=').append(map.get(opt)).append("|");
        result.setCharAt(result.length() - 1, ']');
        return result.toString();
    }

    /**
     * Get option value.
     *
     * @param opt option key.
     * @param defaultValue return this if option is null
     * @return option value.
     */
    public String get(DriverOption opt, String defaultValue) {
        return ObjectUtils.defaultIfNull(get(opt), defaultValue);
    }

    /**
     * Add definitions to capabilities.
     *
     * @param caps capabilities.
     * @return capabilities itself.
     */
    public DesiredCapabilities addCapabilityDefinitions(DesiredCapabilities caps) {
        if (capDefs.length > 0) {
            log.info("Add capability definisions:");
            for (String capDef : capDefs) {
                String[] pair = capDef.split("=", 2);
                log.info("  [{}]=[{}]", pair[0], pair[1]);
                caps.setCapability(pair[0], pair[1]);
            }
        }
        return caps;
    }

    /**
     * Get capability definitions.
     * 
     * @return capability definitions.
     */
    public String[] getCapabilityDefinitions() {
        return capDefs;
    }
}
