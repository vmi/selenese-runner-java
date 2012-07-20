package jp.vmi.selenium.webdriver;

import java.util.IdentityHashMap;

import org.apache.commons.cli.CommandLine;

public class DriverOptions {

    public enum DriverOption {
        PROFILE,
        PROFILE_DIR,
        PROXY,
        PROXY_USER,
        PROXY_PASSWORD,
        NO_PROXY,
    }

    private final IdentityHashMap<DriverOptions.DriverOption, String> map = new IdentityHashMap<DriverOptions.DriverOption, String>();

    public DriverOptions() {
        // no operation
    }

    public DriverOptions(CommandLine cli) {
        for (DriverOption opt : DriverOption.values()) {
            String key = opt.name().toLowerCase().replace('_', '-');
            map.put(opt, cli.getOptionValue(key));
        }
    }

    public String get(DriverOption opt) {
        return map.get(opt);
    }

    public boolean has(DriverOption opt) {
        return map.containsKey(opt);
    }

    public void set(DriverOption opt, String value) {
        map.put(opt, value);
    }
}
