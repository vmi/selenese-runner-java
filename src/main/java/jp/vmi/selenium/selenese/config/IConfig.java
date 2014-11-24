package jp.vmi.selenium.selenese.config;

/**
 * Configuration information.
 */
@SuppressWarnings("javadoc")
public interface IConfig {

    // List of option name.
    public static final String CONFIG = "config";
    public static final String DRIVER = "driver";
    public static final String PROFILE = "profile";
    public static final String PROFILE_DIR = "profile-dir";
    public static final String PROXY = "proxy";
    public static final String PROXY_USER = "proxy-user";
    public static final String PROXY_PASSWORD = "proxy-password";
    public static final String NO_PROXY = "no-proxy";
    public static final String CLI_ARGS = "cli-args";
    public static final String REMOTE_URL = "remote-url";
    public static final String REMOTE_PLATFORM = "remote-platform";
    public static final String REMOTE_BROWSER = "remote-browser";
    public static final String REMOTE_VERSION = "remote-version";
    public static final String HIGHLIGHT = "highlight";
    public static final String SCREENSHOT_DIR = "screenshot-dir";
    public static final String SCREENSHOT_ALL = "screenshot-all";
    public static final String SCREENSHOT_ON_FAIL = "screenshot-on-fail";
    public static final String IGNORE_SCREENSHOT_COMMAND = "ignore-screenshot-command";
    public static final String BASEURL = "baseurl";
    public static final String FIREFOX = "firefox";
    public static final String CHROMEDRIVER = "chromedriver";
    public static final String IEDRIVER = "iedriver";
    public static final String PHANTOMJS = "phantomjs";
    public static final String XML_RESULT = "xml-result";
    public static final String HTML_RESULT = "html-result";
    public static final String TIMEOUT = "timeout";
    public static final String SET_SPEED = "set-speed";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String DEFINE = "define";
    public static final String ROLLUP = "rollup";
    public static final String COOKIE_FILTER = "cookie-filter";
    public static final String COMMAND_FACTORY = "command-factory";
    public static final String HELP = "help";

    /**
     * Test whether the option exists or not.
     *
     * @param opt option name.
     * @return true if exists.
     */
    boolean hasOption(String opt);

    /**
     * Get option value.
     *
     * @param opt option name.
     * @return option value.
     */
    String getOptionValue(String opt);

    /**
     * Get option value.
     *
     * @param opt option name.
     * @param defaultValue default value.
     * @return option value, or defaultvalue if option does not exist.
     */
    String getOptionValue(String opt, String defaultValue);

    /**
     * Get option values.
     *
     * @param opt option name.
     * @return array of option values.
     */
    String[] getOptionValues(String opt);

    /**
     * Get option value as boolean.
     *
     * @param opt option name.
     * @return true if the option is exists or the entry in configuration file is "true"/"on"/"yes".
     */
    boolean getOptionValueAsBoolean(String opt);
}
