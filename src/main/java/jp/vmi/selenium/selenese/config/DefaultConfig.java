package jp.vmi.selenium.selenese.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.text.WordUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.google.common.base.Strings;

import jp.vmi.selenium.selenese.result.Result.Level;
import jp.vmi.selenium.selenese.utils.LangUtils;
import jp.vmi.selenium.selenese.utils.SystemInformation;

import static jp.vmi.selenium.selenese.result.Result.Level.*;

/**
 * Configuration information.
 * <p>
 * If you want to add new options, read the {@link IConfig} documentation.
 * </p>
 */
@SuppressWarnings("javadoc")
public class DefaultConfig implements IConfig {

    public static final int HELP_WIDTH = 80;
    public static final int HELP_PADDING = 2;

    // default values.
    public static final int DEFAULT_TIMEOUT_MILLISEC_N = 30000;
    public static final String DEFAULT_TIMEOUT_MILLISEC = Integer.toString(DEFAULT_TIMEOUT_MILLISEC_N);

    public static final int DEFAULT_MAX_RETRIES = 0;

    // parts of help message.
    private static final String[] HEADER = {
        "Selenese script interpreter implemented by Java.",
        "",
        "Usage: java -jar selenese-runner.jar <option> ... <test-case|test-suite> ..."
    };

    private static String statusListItem(Level level) {
        return String.format("- %d/%d: %s", level.strictExitCode, level.exitCode, level.name());
    }

    private static final String[] FOOTER = {
        "[Note]",
        "*1 It is available if using \"--driver remote --remote-browser firefox\".",
        "",
        "*2 If you want to use basic and/or proxy authentication on Firefox, "
            + "then create new profile, "
            + "install AutoAuth plugin, "
            + "configure all settings, "
            + "access test site with the profile, "
            + "and specify the profile by --profile option.",
        "",
        "*3 Use \"java -cp ..." + File.pathSeparator + "selenese-runner.jar Main --command-factory ...\". ",
        "Because \"java\" command ignores all class path settings, when using \"-jar\" option.",
        "",
        "*4 The list of exit code (strict/normal) is follows:",
        statusListItem(SUCCESS),
        statusListItem(WARNING),
        statusListItem(FAILURE),
        statusListItem(ERROR),
        statusListItem(UNEXECUTED),
        statusListItem(MAX_TIME_EXCEEDED),
        statusListItem(FATAL),
        statusListItem(USAGE)
    };

    private final CmdLineParser parser;

    private final int helpWidth;

    private IConfig parentOptions = null;

    @Option(name = "--config", aliases = "-c", metaVar = "<file>", usage = "load option information from file.")
    private String config;

    // ### BEGIN FIELDS GENERATED FROM config.groovy (*** DO NOT EDIT DIRECTLY ***)

    @Option(name = "--" + DRIVER, aliases = "-d", metaVar = "<driver>", usage = "firefox (default) | chrome | ie | edge | safari | htmlunit | remote | appium | FQCN-of-WebDriverFactory")
    private String driver;

    @Option(name = "--" + HEADLESS, usage = "use headless mode if driver is supported (currently, Chrome and Firefox)")
    private Boolean headless;

    @Option(name = "--" + PROFILE, aliases = "-p", metaVar = "<name>", usage = "profile name (Firefox only *1)")
    private String profile;

    @Option(name = "--" + PROFILE_DIR, aliases = "-P", metaVar = "<dir>", usage = "profile directory (Firefox only *1)")
    private String profileDir;

    @Option(name = "--" + CHROME_EXPERIMENTAL_OPTIONS, metaVar = "<file>", usage = "path to json file specify experimental options for chrome (Chrome only *1)")
    private String chromeExperimentalOptions;

    @Option(name = "--" + CHROME_EXTENSION, metaVar = "<file>", usage = "chrome extension file (multiple, Chrome only *1)")
    private String[] chromeExtension;

    @Option(name = "--" + PROXY_TYPE, metaVar = "<proxy-type>", usage = "proxy type (manual (default if set --proxy) | pac | autodetect | system)")
    private String proxyType;

    @Option(name = "--" + PROXY, metaVar = "<proxy>", usage = "[manual] proxy host and port (HOST:PORT) (excepting IE) / [pac] PAC URL")
    private String proxy;

    @Option(name = "--" + PROXY_USER, metaVar = "<user>", usage = "proxy username (HtmlUnit only *2)")
    private String proxyUser;

    @Option(name = "--" + PROXY_PASSWORD, metaVar = "<password>", usage = "proxy password (HtmlUnit only *2)")
    private String proxyPassword;

    @Option(name = "--" + NO_PROXY, metaVar = "<hosts>", usage = "no-proxy hosts")
    private String noProxy;

    @Option(name = "--" + CLI_ARGS, metaVar = "<arg>", usage = "add command line arguments at starting up driver (multiple)")
    private String[] cliArgs;

    @Option(name = "--" + REMOTE_URL, metaVar = "<url>", usage = "Remote test runner URL (Remote only)")
    private String remoteUrl;

    @Option(name = "--" + REMOTE_PLATFORM, metaVar = "<platform>", usage = "Desired remote platform (Remote only)")
    private String remotePlatform;

    @Option(name = "--" + REMOTE_BROWSER, metaVar = "<browser>", usage = "Desired remote browser (Remote only)")
    private String remoteBrowser;

    @Option(name = "--" + REMOTE_VERSION, metaVar = "<browser-version>", usage = "Desired remote browser version (Remote only)")
    private String remoteVersion;

    @Option(name = "--" + HIGHLIGHT, aliases = "-H", usage = "highlight locator always.")
    private Boolean highlight;

    @Option(name = "--" + INTERACTIVE, aliases = "-i", usage = "interactive mode.")
    private Boolean interactive;

    @Option(name = "--" + SCREENSHOT_DIR, aliases = "-s", metaVar = "<dir>", usage = "override captureEntirePageScreenshot directory.")
    private String screenshotDir;

    @Option(name = "--" + SCREENSHOT_ALL, aliases = "-S", metaVar = "<dir>", usage = "take screenshot at all commands to specified directory.")
    private String screenshotAll;

    @Option(name = "--" + SCREENSHOT_ON_FAIL, metaVar = "<dir>", usage = "take screenshot on fail commands to specified directory.")
    private String screenshotOnFail;

    @Option(name = "--" + SCREENSHOT_SCROLL_TIMEOUT, metaVar = "<timeout>", usage = "set scroll timeout (ms) for taking screenshot. (default: 100)")
    private String screenshotScrollTimeout;

    @Option(name = "--" + IGNORE_SCREENSHOT_COMMAND, usage = "ignore captureEntirePageScreenshot command.")
    private Boolean ignoreScreenshotCommand;

    @Option(name = "--" + BASEURL, aliases = "-b", metaVar = "<baseURL>", usage = "override base URL set in selenese.")
    private String baseurl;

    @Option(name = "--" + FIREFOX, metaVar = "<path>", usage = "path to 'firefox' binary. (implies '--driver firefox')")
    private String firefox;

    @Option(name = "--" + GECKODRIVER, metaVar = "<path>", usage = "path to 'geckodriver' binary. (implies '--driver firefox')")
    private String geckodriver;

    @Option(name = "--" + CHROMEDRIVER, metaVar = "<path>", usage = "path to 'chromedriver' binary. (implies '--driver chrome')")
    private String chromedriver;

    @Option(name = "--" + IEDRIVER, metaVar = "<path>", usage = "path to 'IEDriverServer' binary. (implies '--driver ie')")
    private String iedriver;

    @Option(name = "--" + EDGEDRIVER, metaVar = "<path>", usage = "path to Edge 'WebDriver' binary. (implies '--driver edge')")
    private String edgedriver;

    @Option(name = "--" + XML_RESULT, metaVar = "<dir>", usage = "output XML JUnit results to specified directory.")
    private String xmlResult;

    @Option(name = "--" + HTML_RESULT, metaVar = "<dir>", usage = "output HTML results to specified directory.")
    private String htmlResult;

    @Option(name = "--" + TIMEOUT, aliases = "-t", metaVar = "<timeout>", usage = "set timeout (ms) for waiting. (default: " + DEFAULT_TIMEOUT_MILLISEC_N + " ms)")
    private String timeout;

    @Option(name = "--" + MAX_RETRIES, metaVar = "<maxRetries>", usage = "set maximum number of retries for a given step. (default: " + DEFAULT_MAX_RETRIES + ")")
    private String maxRetries;

    @Option(name = "--" + SET_SPEED, metaVar = "<speed>", usage = "same as executing setSpeed(ms) command first.")
    private String setSpeed;

    @Option(name = "--" + HEIGHT, metaVar = "<height>", usage = "set initial height. (excluding mobile)")
    private String height;

    @Option(name = "--" + WIDTH, metaVar = "<width>", usage = "set initial width. (excluding mobile)")
    private String width;

    @Option(name = "--" + ALERTS_POLICY, usage = "The default behaviour for unexpected alerts (accept/ignore/dismiss)")
    private String alertsPolicy;

    @Option(name = "--" + DEFINE, aliases = "-D", metaVar = "<key>[:<type>][+]=<value>",
        usage = "define parameters for capabilities. <type> is a value type: str (default), int or bool (multiple)")
    private String[] define;

    @Option(name = "--" + VAR, aliases = "-V", metaVar = "<var-name>=<json-value>", usage = "set JSON value to variable with a specified name. (multiple)")
    private String[] var;

    @Option(name = "--" + ROLLUP, metaVar = "<file>", usage = "define rollup rule by JavaScript. (multiple)")
    private String[] rollup;

    @Option(name = "--" + COOKIE_FILTER, metaVar = "<+RE|-RE>", usage = "filter cookies to log by RE matching the name. (\"+\" is passing, \"-\" is suppressing)")
    private String cookieFilter;

    @Option(name = "--" + LOG_FILTER, metaVar = "<+type|-type>",
        usage = "filter the logging information by the specified type. (multiple. \"+\" is passing, \"-\" is suppressing. type: cookie, title, url, pageinfo(= cookie & title & url))")
    private String[] logFilter;

    @Option(name = "--" + COMMAND_FACTORY, metaVar = "<FQCN>", usage = "register user defined command factory. (See Note *3)")
    private String commandFactory;

    @Option(name = "--" + NO_EXIT, usage = "don't call System.exit at end.")
    private Boolean noExit;

    @Option(name = "--" + STRICT_EXIT_CODE, usage = "return strict exit code, reflected by selenese command results at end. (See Note *4)")
    private Boolean strictExitCode;

    @Option(name = "--" + MAX_TIME, metaVar = "<max-time>", usage = "Maximum time in seconds that you allow the entire operation to take.")
    private String maxTime;

    @Option(name = "--" + NO_REPLACE_ALERT_METHOD, usage = "disable replacement of alert methods")
    private Boolean noReplaceAlertMethod;

    @Option(name = "--" + HELP, aliases = "-h", usage = "show this message.")
    private Boolean help;

    // ### END FIELDS GENERATED FROM config.groovy (*** DO NOT EDIT DIRECTLY ***)

    @Argument
    private String[] args = LangUtils.EMPTY_STRING_ARRAY;

    private final OptionMap optionMap = new OptionMap(this);

    /**
     * Constructor.
     *
     * @param args command line arguments.
     */
    public DefaultConfig(String... args) {
        String columns = System.getProperty("columns", System.getenv("COLUMNS"));
        helpWidth = (columns != null && columns.matches("\\d+")) ? Integer.parseInt(columns) : HELP_WIDTH;
        ParserProperties props = ParserProperties.defaults()
            .withOptionSorter(null)
            .withUsageWidth(helpWidth)
            .withShowDefaults(false);
        parser = new CmdLineParser(this, props);
        if (args.length > 0)
            parseCommandLine(args);
    }

    // ### BEGIN GETTERS & SETTERS GENERATED FROM config.groovy (*** DO NOT EDIT DIRECTLY ***)

    @Override
    public String getDriver() {
        return driver != null ? driver : (parentOptions != null ? parentOptions.getDriver() : null);
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    @Override
    public boolean isHeadless() {
        return headless != null ? headless : (parentOptions != null ? parentOptions.isHeadless() : false);
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    @Override
    public String getProfile() {
        return profile != null ? profile : (parentOptions != null ? parentOptions.getProfile() : null);
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String getProfileDir() {
        return profileDir != null ? profileDir : (parentOptions != null ? parentOptions.getProfileDir() : null);
    }

    public void setProfileDir(String profileDir) {
        this.profileDir = profileDir;
    }

    @Override
    public String getChromeExperimentalOptions() {
        return chromeExperimentalOptions != null ? chromeExperimentalOptions : (parentOptions != null ? parentOptions.getChromeExperimentalOptions() : null);
    }

    public void setChromeExperimentalOptions(String chromeExperimentalOptions) {
        this.chromeExperimentalOptions = chromeExperimentalOptions;
    }

    @Override
    public String[] getChromeExtension() {
        return chromeExtension != null ? chromeExtension : (parentOptions != null ? parentOptions.getChromeExtension() : null);
    }

    public void addChromeExtension(String chromeExtensionItem) {
        this.chromeExtension = ArrayUtils.add(this.chromeExtension, chromeExtensionItem);
    }

    @Override
    public String getProxyType() {
        return proxyType != null ? proxyType : (parentOptions != null ? parentOptions.getProxyType() : null);
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    @Override
    public String getProxy() {
        return proxy != null ? proxy : (parentOptions != null ? parentOptions.getProxy() : null);
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    @Override
    public String getProxyUser() {
        return proxyUser != null ? proxyUser : (parentOptions != null ? parentOptions.getProxyUser() : null);
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Override
    public String getProxyPassword() {
        return proxyPassword != null ? proxyPassword : (parentOptions != null ? parentOptions.getProxyPassword() : null);
    }

    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public String getNoProxy() {
        return noProxy != null ? noProxy : (parentOptions != null ? parentOptions.getNoProxy() : null);
    }

    public void setNoProxy(String noProxy) {
        this.noProxy = noProxy;
    }

    @Override
    public String[] getCliArgs() {
        return cliArgs != null ? cliArgs : (parentOptions != null ? parentOptions.getCliArgs() : null);
    }

    public void addCliArgs(String cliArgsItem) {
        this.cliArgs = ArrayUtils.add(this.cliArgs, cliArgsItem);
    }

    @Override
    public String getRemoteUrl() {
        return remoteUrl != null ? remoteUrl : (parentOptions != null ? parentOptions.getRemoteUrl() : null);
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    @Override
    public String getRemotePlatform() {
        return remotePlatform != null ? remotePlatform : (parentOptions != null ? parentOptions.getRemotePlatform() : null);
    }

    public void setRemotePlatform(String remotePlatform) {
        this.remotePlatform = remotePlatform;
    }

    @Override
    public String getRemoteBrowser() {
        return remoteBrowser != null ? remoteBrowser : (parentOptions != null ? parentOptions.getRemoteBrowser() : null);
    }

    public void setRemoteBrowser(String remoteBrowser) {
        this.remoteBrowser = remoteBrowser;
    }

    @Override
    public String getRemoteVersion() {
        return remoteVersion != null ? remoteVersion : (parentOptions != null ? parentOptions.getRemoteVersion() : null);
    }

    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    @Override
    public boolean isHighlight() {
        return highlight != null ? highlight : (parentOptions != null ? parentOptions.isHighlight() : false);
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    @Override
    public boolean isInteractive() {
        return interactive != null ? interactive : (parentOptions != null ? parentOptions.isInteractive() : false);
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    @Override
    public String getScreenshotDir() {
        return screenshotDir != null ? screenshotDir : (parentOptions != null ? parentOptions.getScreenshotDir() : null);
    }

    public void setScreenshotDir(String screenshotDir) {
        this.screenshotDir = screenshotDir;
    }

    @Override
    public String getScreenshotAll() {
        return screenshotAll != null ? screenshotAll : (parentOptions != null ? parentOptions.getScreenshotAll() : null);
    }

    public void setScreenshotAll(String screenshotAll) {
        this.screenshotAll = screenshotAll;
    }

    @Override
    public String getScreenshotOnFail() {
        return screenshotOnFail != null ? screenshotOnFail : (parentOptions != null ? parentOptions.getScreenshotOnFail() : null);
    }

    public void setScreenshotOnFail(String screenshotOnFail) {
        this.screenshotOnFail = screenshotOnFail;
    }

    @Override
    public String getScreenshotScrollTimeout() {
        return screenshotScrollTimeout != null ? screenshotScrollTimeout : (parentOptions != null ? parentOptions.getScreenshotScrollTimeout() : null);
    }

    public void setScreenshotScrollTimeout(String screenshotScrollTimeout) {
        this.screenshotScrollTimeout = screenshotScrollTimeout;
    }

    @Override
    public boolean isIgnoreScreenshotCommand() {
        return ignoreScreenshotCommand != null ? ignoreScreenshotCommand : (parentOptions != null ? parentOptions.isIgnoreScreenshotCommand() : false);
    }

    public void setIgnoreScreenshotCommand(boolean ignoreScreenshotCommand) {
        this.ignoreScreenshotCommand = ignoreScreenshotCommand;
    }

    @Override
    public String getBaseurl() {
        return baseurl != null ? baseurl : (parentOptions != null ? parentOptions.getBaseurl() : null);
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    @Override
    public String getFirefox() {
        return firefox != null ? firefox : (parentOptions != null ? parentOptions.getFirefox() : null);
    }

    public void setFirefox(String firefox) {
        this.firefox = firefox;
    }

    @Override
    public String getGeckodriver() {
        return geckodriver != null ? geckodriver : (parentOptions != null ? parentOptions.getGeckodriver() : null);
    }

    public void setGeckodriver(String geckodriver) {
        this.geckodriver = geckodriver;
    }

    @Override
    public String getChromedriver() {
        return chromedriver != null ? chromedriver : (parentOptions != null ? parentOptions.getChromedriver() : null);
    }

    public void setChromedriver(String chromedriver) {
        this.chromedriver = chromedriver;
    }

    @Override
    public String getIedriver() {
        return iedriver != null ? iedriver : (parentOptions != null ? parentOptions.getIedriver() : null);
    }

    public void setIedriver(String iedriver) {
        this.iedriver = iedriver;
    }

    @Override
    public String getEdgedriver() {
        return edgedriver != null ? edgedriver : (parentOptions != null ? parentOptions.getEdgedriver() : null);
    }

    public void setEdgedriver(String edgedriver) {
        this.edgedriver = edgedriver;
    }

    @Override
    public String getXmlResult() {
        return xmlResult != null ? xmlResult : (parentOptions != null ? parentOptions.getXmlResult() : null);
    }

    public void setXmlResult(String xmlResult) {
        this.xmlResult = xmlResult;
    }

    @Override
    public String getHtmlResult() {
        return htmlResult != null ? htmlResult : (parentOptions != null ? parentOptions.getHtmlResult() : null);
    }

    public void setHtmlResult(String htmlResult) {
        this.htmlResult = htmlResult;
    }

    @Override
    public String getTimeout() {
        return timeout != null ? timeout : (parentOptions != null ? parentOptions.getTimeout() : null);
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getMaxRetries() {
        return maxRetries != null ? maxRetries : (parentOptions != null ? parentOptions.getMaxRetries() : null);
    }

    public void setMaxRetries(String maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public String getSetSpeed() {
        return setSpeed != null ? setSpeed : (parentOptions != null ? parentOptions.getSetSpeed() : null);
    }

    public void setSetSpeed(String setSpeed) {
        this.setSpeed = setSpeed;
    }

    @Override
    public String getHeight() {
        return height != null ? height : (parentOptions != null ? parentOptions.getHeight() : null);
    }

    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String getWidth() {
        return width != null ? width : (parentOptions != null ? parentOptions.getWidth() : null);
    }

    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String getAlertsPolicy() {
        return alertsPolicy != null ? alertsPolicy : (parentOptions != null ? parentOptions.getAlertsPolicy() : null);
    }

    public void setAlertsPolicy(String alertsPolicy) {
        this.alertsPolicy = alertsPolicy;
    }

    @Override
    public String[] getDefine() {
        return define != null ? define : (parentOptions != null ? parentOptions.getDefine() : null);
    }

    public void addDefine(String defineItem) {
        this.define = ArrayUtils.add(this.define, defineItem);
    }

    @Override
    public String[] getVar() {
        return var != null ? var : (parentOptions != null ? parentOptions.getVar() : null);
    }

    public void addVar(String varItem) {
        this.var = ArrayUtils.add(this.var, varItem);
    }

    @Override
    public String[] getRollup() {
        return rollup != null ? rollup : (parentOptions != null ? parentOptions.getRollup() : null);
    }

    public void addRollup(String rollupItem) {
        this.rollup = ArrayUtils.add(this.rollup, rollupItem);
    }

    @Override
    public String getCookieFilter() {
        return cookieFilter != null ? cookieFilter : (parentOptions != null ? parentOptions.getCookieFilter() : null);
    }

    public void setCookieFilter(String cookieFilter) {
        this.cookieFilter = cookieFilter;
    }

    @Override
    public String[] getLogFilter() {
        return logFilter != null ? logFilter : (parentOptions != null ? parentOptions.getLogFilter() : null);
    }

    public void addLogFilter(String logFilterItem) {
        this.logFilter = ArrayUtils.add(this.logFilter, logFilterItem);
    }

    @Override
    public String getCommandFactory() {
        return commandFactory != null ? commandFactory : (parentOptions != null ? parentOptions.getCommandFactory() : null);
    }

    public void setCommandFactory(String commandFactory) {
        this.commandFactory = commandFactory;
    }

    @Override
    public boolean isNoExit() {
        return noExit != null ? noExit : (parentOptions != null ? parentOptions.isNoExit() : false);
    }

    public void setNoExit(boolean noExit) {
        this.noExit = noExit;
    }

    @Override
    public boolean isStrictExitCode() {
        return strictExitCode != null ? strictExitCode : (parentOptions != null ? parentOptions.isStrictExitCode() : false);
    }

    public void setStrictExitCode(boolean strictExitCode) {
        this.strictExitCode = strictExitCode;
    }

    @Override
    public String getMaxTime() {
        return maxTime != null ? maxTime : (parentOptions != null ? parentOptions.getMaxTime() : null);
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    @Override
    public boolean isNoReplaceAlertMethod() {
        return noReplaceAlertMethod != null ? noReplaceAlertMethod : (parentOptions != null ? parentOptions.isNoReplaceAlertMethod() : false);
    }

    public void setNoReplaceAlertMethod(boolean noReplaceAlertMethod) {
        this.noReplaceAlertMethod = noReplaceAlertMethod;
    }

    @Override
    public boolean isHelp() {
        return help != null ? help : (parentOptions != null ? parentOptions.isHelp() : false);
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    // ### END GETTERS & SETTERS GENERATED FROM config.groovy (*** DO NOT EDIT DIRECTLY ***)

    @Override
    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Parse command line arguments.
     *
     * @param args command line arguments.
     * @return parsed command line information.
     * @throws IllegalArgumentException invalid options.
     */
    public IConfig parseCommandLine(String... args) {
        try {
            parser.parseArgument(args);
            if (config != null)
                parentOptions = new DefaultConfig().loadFrom(config);
            if (args == null)
                args = LangUtils.EMPTY_STRING_ARRAY;
            return this;
        } catch (CmdLineException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    // Comment, KEY(1):VALUE(2), or NEXT_VALUE(3).
    private static final Pattern RE = Pattern.compile("#.*|([\\w\\-]+)\\s*:\\s*(.*?)\\s*|\\s+(.*?)\\s*");

    /**
     * load configuration from file.
     *
     * @param file configuration file name.
     * @return DefaultConfig object itself.
     */
    public IConfig loadFrom(String file) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            int cnt = 0;
            String line;
            String currentKey = null;
            while ((line = r.readLine()) != null) {
                cnt++;
                if (line.isEmpty())
                    continue;
                Matcher matcher = RE.matcher(line);
                if (!matcher.matches())
                    throw new RuntimeException(file + ":" + cnt + ": Invalid format: " + line);
                String key = matcher.group(1);
                if (key != null) {
                    currentKey = key;
                    String value = matcher.group(2);
                    if (!Strings.isNullOrEmpty(value))
                        setOrAddOptionValue(currentKey, value);
                } else if (currentKey != null) {
                    String value = matcher.group(3);
                    if (!Strings.isNullOrEmpty(value))
                        setOrAddOptionValue(currentKey, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Set or add option value by name.
     *
     * @param name option name.
     * @param value option value.
     */
    public void setOrAddOptionValue(String name, String value) {
        Class<?> type = optionMap.get(name).type;
        if (type == String.class) {
            set(name, value);
        } else if (type == String[].class) {
            add(name, value);
        } else if (type == boolean.class || type == Boolean.class) {
            set(name, BooleanUtils.toBoolean(value));
        } else {
            throw new UnsupportedOperationException(String.format("Can't set \"%s\" to option \"%s\" (%s)", value, name, type));
        }
    }

    @Override
    public <T> T get(String name) {
        return optionMap.get(name).get(this);
    }

    /**
     * Get option value by name.
     * @param name option name.
     * @param defaultValue default option value.
     * @return option value.
     */
    @Override
    public <T> T get(String name, T defaultValue) {
        T result = optionMap.get(name).get(this);
        return result != null ? result : defaultValue;
    }

    /**
     * Set option value by name.
     * @param name option name.
     * @param value option value.
     */
    public void set(String name, Object value) {
        optionMap.get(name).set(this, value);
    }

    /**
     * Add value to option which has multiple parameters by name.
     * @param name option name.
     * @param value option value.
     */
    public void add(String name, String value) {
        optionMap.get(name).add(this, value);
    }

    private static final Pattern NOTE_RE = Pattern.compile("(\\*\\d+\\s+)(.*)");

    /**
     * Show help message.
     *
     * @param pw PrintWriter.
     * @param title program title.
     * @param version program version.
     * @param cmdName command name.
     * @param msgs messages.
     */
    public void showHelp(PrintWriter pw, String title, String version, String cmdName, String... msgs) {
        if (msgs.length > 0) {
            for (String msg : msgs)
                System.out.println(msg);
            System.out.println();
        }
        int width = helpWidth - HELP_PADDING;
        String seleniumVersion = SystemInformation.getInstance().getSeleniumVersion();
        pw.println(WordUtils.wrap(title + " " + version + " (with Selenium " + seleniumVersion + ")", width));
        pw.println();
        for (String line : HEADER) {
            if (line.isEmpty())
                pw.println();
            else
                pw.println(WordUtils.wrap(line, width));
        }
        pw.println();
        parser.printUsage(pw, null);
        pw.println();
        String padding = "";
        for (String line : FOOTER) {
            if (line.isEmpty()) {
                padding = "";
                pw.println();
            } else {
                Matcher m = NOTE_RE.matcher(line);
                if (m.matches()) {
                    String mark = m.group(1);
                    padding = Strings.repeat(" ", mark.length());
                    String desc = WordUtils.wrap(m.group(2), width - mark.length(), "\n", false).replace("\n", System.lineSeparator() + padding);
                    pw.print(mark);
                    pw.print(desc);
                    pw.println();
                } else {
                    pw.print(padding);
                    pw.println(WordUtils.wrap(line, width - padding.length(), "\n", false).replace("\n", System.lineSeparator() + padding));
                }
            }
        }
        pw.flush();
    }
}
