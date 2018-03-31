package jp.vmi.selenium.selenese.config;

/**
 * Configuration information.
 *
 * <p>
 * If you want to add new option, you need edit the following classes:
 * </p>
 * <ul>
 * <li>{@link jp.vmi.selenium.selenese.config.DefaultConfig}</li>
 * <li>{@link jp.vmi.selenium.selenese.config.IConfig}</li>
 * <li>{@link jp.vmi.selenium.webdriver.DriverOptions} (for WebDriver specific option)</li>
 * <li>{@link jp.vmi.selenium.webdriver.DriverOptions.DriverOption} (for WebDriver specific option)</li>
 * </ul>
 */
@SuppressWarnings("javadoc")
public interface IConfig {

    // List of option name.
    // public static final String CONFIG = "config";
    public static final String DRIVER = "driver";
    public static final String HEADLESS = "headless";
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
    public static final String INTERACTIVE = "interactive";
    public static final String SCREENSHOT_DIR = "screenshot-dir";
    public static final String SCREENSHOT_ALL = "screenshot-all";
    public static final String SCREENSHOT_ON_FAIL = "screenshot-on-fail";
    public static final String IGNORE_SCREENSHOT_COMMAND = "ignore-screenshot-command";
    public static final String BASEURL = "baseurl";
    public static final String FIREFOX = "firefox";
    public static final String GECKODRIVER = "geckodriver";
    public static final String CHROMEDRIVER = "chromedriver";
    public static final String IEDRIVER = "iedriver";
    public static final String EDGEDRIVER = "edgedriver";
    public static final String PHANTOMJS = "phantomjs";
    public static final String XML_RESULT = "xml-result";
    public static final String HTML_RESULT = "html-result";
    public static final String TIMEOUT = "timeout";
    public static final String SET_SPEED = "set-speed";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String ALERTS_POLICY = "alerts-policy";
    public static final String DEFINE = "define";
    public static final String VAR = "var";
    public static final String ROLLUP = "rollup";
    public static final String COOKIE_FILTER = "cookie-filter";
    public static final String LOG_FILTER = "log-filter";
    public static final String COMMAND_FACTORY = "command-factory";
    public static final String NO_EXIT = "no-exit";
    public static final String STRICT_EXIT_CODE = "strict-exit-code";
    public static final String MAX_TIME = "max-time";
    public static final String HELP = "help";
    public static final String ALERTS_POLICY = "alerts-policy";

    /**
     * Get command line arguments without parsed options.
     *
     * @return command line arguments.
     */
    String[] getArgs();

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

    boolean isHeadless();

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

    boolean isInteractive();

    String getScreenshotDir();

    String getScreenshotAll();

    String getScreenshotOnFail();

    boolean isIgnoreScreenshotCommand();

    String getBaseurl();

    String getFirefox();

    String getGeckodriver();

    String getChromedriver();

    String getIedriver();

    String getEdgedriver();

    String getPhantomjs();

    String getXmlResult();

    String getHtmlResult();

    String getTimeout();

    String getSetSpeed();

    String getHeight();

    String getWidth();

    String[] getDefine();

    String[] getVar();

    String[] getRollup();

    String getCookieFilter();

    String[] getLogFilter();

    String getCommandFactory();

    boolean isNoExit();

    boolean isStrictExitCode();

    String getMaxTime();

    boolean isHelp();

    String getAlertsPolicy();
}
