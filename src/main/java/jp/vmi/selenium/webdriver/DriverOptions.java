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
        for (DriverOption key : DriverOption.values())
            map.put(key, cli.getOptionValue(key.name()));
    }

    public String get(DriverOption key) {
        return map.get(key);
    }

    public boolean has(DriverOption key) {
        return map.containsKey(key);
    }

    public void set(DriverOption key, String value) {
        map.put(key, value);
    }
}
