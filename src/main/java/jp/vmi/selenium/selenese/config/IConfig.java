package jp.vmi.selenium.selenese.config;

/**
 * Configuration information.
 */
@SuppressWarnings("javadoc")
public interface IConfig {

    // List of option name.
    // public static final String CONFIG = "config";
    public static final String DRIVER = "driver";
    public static final String PROFILE = "profile";
    public static final String PROFILE_DIR = "profile-dir";
    public static final String CHROME_EXTENSION = "chrome-extension";
    public static final String CHROME_EXPERIMENTAL_OPTIONS = "chrome-experimental-options";
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
    public static final String NO_EXIT = "no-exit";
    public static final String STRICT_EXIT_CODE = "strict-exit-code";
    public static final String HELP = "help";

    /**
     * Get command line arguments without parsed options.
     *
     * @return command line arguments.
     */
    String[] getArgs();

    /**
     * Test whether the option exists or not. (old style)
     *
     * @param opt option name.
     * @return true if exists.
     *
     * @deprecated Use getXXX() != null.
     */
    @Deprecated
    boolean hasOption(String opt);

    /**
     * Get option value. (old style)
     *
     * @param opt option name.
     * @return option value.
     *
     * @deprecated Use {@link #get(String)} or getXXX()
     */
    @Deprecated
    String getOptionValue(String opt);

    /**
     * Get option value or default value. (old style)
     *
     * @param opt option name.
     * @param defaultValue default value.
     * @return option value, or defaultvalue if option does not exist.
     *
     * @deprecated Use {@link #get(String, Object)} or getXXX() with null check.
     */
    @Deprecated
    String getOptionValue(String opt, String defaultValue);

    /**
     * Get option values. (old style)
     *
     * @param opt option name.
     * @return array of option values.
     *
     * @deprecated Use {@link #get(String)} or getXXX() as String[].
     */
    @Deprecated
    String[] getOptionValues(String opt);

    /**
     * Get option value as boolean. (old style)
     *
     * @param opt option name.
     * @return true if the option is exists or the entry in configuration file is "true"/"on"/"yes".
     *
     * @deprecated Use {@link #get(String)} or getXXX() as boolean.
     */
    @Deprecated
    boolean getOptionValueAsBoolean(String opt);

    /**
     * Get option value.
     *
     * @param <T> type of option value.
     * @param opt option name.
     * @return option value.
     */
    <T> T get(String opt);

    /**
     * Get option value or default value.
     *
     * @param <T> type of option value.
     * @param opt option name.
     * @param defaultValue default value.
     * @return option value.
     */
    <T> T get(String opt, T defaultValue);

    String getDriver();

    String getProfile();

    String getProfileDir();

    String getChromeExperimentalOptions();

    String[] getChromeExtension();

    String getProxy();

    String getProxyUser();

    String getProxyPassword();

    String getNoProxy();

    String[] getCliArgs();

    String getRemoteUrl();

    String getRemotePlatform();

    String getRemoteBrowser();

    String getRemoteVersion();

    boolean isHighlight();

    String getScreenshotDir();

    String getScreenshotAll();

    String getScreenshotOnFail();

    boolean isIgnoreScreenshotCommand();

    String getBaseurl();

    String getFirefox();

    String getChromedriver();

    String getIedriver();

    String getPhantomjs();

    String getXmlResult();

    String getHtmlResult();

    String getTimeout();

    String getSetSpeed();

    String getHeight();

    String getWidth();

    String[] getDefine();

    String[] getRollup();

    String getCookieFilter();

    String getCommandFactory();

    boolean isNoExit();

    boolean isStrictExitCode();

    boolean isHelp();
}
