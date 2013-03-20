package jp.vmi.selenium.webdriver;

import java.util.IdentityHashMap;

import org.apache.commons.cli.CommandLine;

/**
 * Options for WebDriver.
 */
public class DriverOptions {

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
        /** --remote-platform */
        REMOTE_PLATFORM,
        /** --remote-browser */
        REMOTE_BROWSER,
        /** --remote-version */
        REMOTE_VERSION,
        /** --remote-url */
        REMOTE_URL,
    }

    private final IdentityHashMap<DriverOptions.DriverOption, String> map = new IdentityHashMap<DriverOptions.DriverOption, String>();

    /**
     * Constructs empty options.
     */
    public DriverOptions() {
        // no operation
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
}
