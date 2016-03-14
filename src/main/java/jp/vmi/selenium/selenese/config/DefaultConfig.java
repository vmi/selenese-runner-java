package jp.vmi.selenium.selenese.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import jp.vmi.selenium.selenese.result.Result.Level;
import jp.vmi.selenium.selenese.utils.LangUtils;

import static jp.vmi.selenium.selenese.result.Result.Level.*;
import static org.apache.commons.lang3.SystemUtils.*;

/**
 * Configuration information.
 */
@SuppressWarnings("javadoc")
public class DefaultConfig implements IConfig {

    private static final int HELP_WIDTH = 80;
    private static final int HELP_PADDING = 2;

    // default values.
    public static final int DEFAULT_TIMEOUT_MILLISEC_N = 30000;
    public static final String DEFAULT_TIMEOUT_MILLISEC = Integer.toString(DEFAULT_TIMEOUT_MILLISEC_N);

    // parts of help message.
    private static final String[] HEADER = {
        "Selenese script interpreter implemented by Java.",
        "",
        "Usage: java -jar selenese-runner.jar <option> ... <test-case|test-suite> ..."
    };

    private static String statusListItem(Level level) {
        return "- " + level.strictExitCode + ": " + level.name();
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
        "*3 Use \"java -cp ..." + PATH_SEPARATOR + "selenese-runner.jar Main --command-factory ...\". ",
        "Because \"java\" command ignores all class path settings, when using \"-jar\" option.",
        "",
        "*4 The list of strict exit code is follows:",
        statusListItem(SUCCESS),
        statusListItem(WARNING),
        statusListItem(FAILURE),
        statusListItem(ERROR),
        statusListItem(UNEXECUTED)
    };

    private final CmdLineParser parser;

    private final int helpWidth;

    private IConfig parentOptions = null;

    @Option(name = "--config", aliases = "-c", metaVar = "<file>", usage = "load option information from file.")
    private String config;

    @Option(name = "--driver", aliases = "-d", metaVar = "<driver>",
        usage = "firefox (default) | chrome | ie | safari | htmlunit | phantomjs | remote | appium | FQCN-of-WebDriverFactory")
    private String driver;

    @Option(name = "--profile", aliases = "-p", metaVar = "<name>", usage = "profile name (Firefox only *1)")
    private String profile;

    @Option(name = "--profile-dir", aliases = "-P", metaVar = "<dir>", usage = "profile directory (Firefox only *1)")
    private String profileDir;

    @Option(name = "--chrome-experimental-options", metaVar = "<file>", usage = "path to json file specify experimental options for chrome (Chrome only *1)")
    private String chromeExperimentalOptions;

    @Option(name = "--chrome-extension", metaVar = "<file>", usage = "chrome extension file (multiple, Chrome only *1)")
    private String[] chromeExtension;

    @Option(name = "--proxy", metaVar = "<proxy>", usage = "proxy host and port (HOST:PORT) (excepting IE)")
    private String proxy;

    @Option(name = "--proxy-user", metaVar = "<user>", usage = "proxy username (HtmlUnit only *2)")
    private String proxyUser;

    @Option(name = "--proxy-password", metaVar = "<password>", usage = "proxy password (HtmlUnit only *2)")
    private String proxyPassword;

    @Option(name = "--no-proxy", metaVar = "<hosts>", usage = "no-proxy hosts")
    private String noProxy;

    @Option(name = "--cli-args", metaVar = "<arg>", usage = "add command line arguments at starting up driver (multiple)")
    private String[] cliArgs;

    @Option(name = "--remote-url", metaVar = "<url>", usage = "Remote test runner URL (Remote only)")
    private String remoteUrl;

    @Option(name = "--remote-platform", metaVar = "<platform>", usage = "Desired remote platform (Remote only)")
    private String remotePlatform;

    @Option(name = "--remote-browser", metaVar = "<browser>", usage = "Desired remote browser (Remote only)")
    private String remoteBrowser;

    @Option(name = "--remote-version", metaVar = "<browser-version>", usage = "Desired remote browser version (Remote only)")
    private String remoteVersion;

    @Option(name = "--highlight", aliases = "-H", usage = "highlight locator always.")
    private Boolean highlight;

    @Option(name = "--screenshot-dir", aliases = "-s", metaVar = "<dir>", usage = "override captureEntirePageScreenshot directory.")
    private String screenshotDir;

    @Option(name = "--screenshot-all", aliases = "-S", metaVar = "<dir>", usage = "take screenshot at all commands to specified directory.")
    private String screenshotAll;

    @Option(name = "--screenshot-on-fail", metaVar = "<dir>", usage = "take screenshot on fail commands to specified directory.")
    private String screenshotOnFail;

    @Option(name = "--ignore-screenshot-command", usage = "ignore captureEntirePageScreenshot command.")
    private Boolean ignoreScreenshotCommand;

    @Option(name = "--baseurl", aliases = "-b", metaVar = "<baseURL>", usage = "override base URL set in selenese.")
    private String baseurl;

    @Option(name = "--firefox", metaVar = "<path>", usage = "path to 'firefox' binary. (implies '--driver firefox')")
    private String firefox;

    @Option(name = "--chromedriver", metaVar = "<path>", usage = "path to 'chromedriver' binary. (implies '--driver chrome')")
    private String chromedriver;

    @Option(name = "--iedriver", metaVar = "<path>", usage = "path to 'IEDriverServer' binary. (implies '--driver ie')")
    private String iedriver;

    @Option(name = "--phantomjs", metaVar = "<path>", usage = "path to 'phantomjs' binary. (implies '--driver phantomjs')")
    private String phantomjs;

    @Option(name = "--xml-result", metaVar = "<dir>", usage = "output XML JUnit results to specified directory.")
    private String xmlResult;

    @Option(name = "--html-result", metaVar = "<dir>", usage = "output HTML results to specified directory.")
    private String htmlResult;

    @Option(name = "--timeout", aliases = "-t", metaVar = "<timeout>", usage = "set timeout (ms) for waiting. (default: " + DEFAULT_TIMEOUT_MILLISEC_N + " ms)")
    private String timeout;

    @Option(name = "--set-speed", metaVar = "<speed>", usage = "same as executing setSpeed(ms) command first.")
    private String setSpeed;

    @Option(name = "--height", metaVar = "<height>", usage = "set initial height. (excluding mobile)")
    private String height;

    @Option(name = "--width", metaVar = "<width>", usage = "set initial width. (excluding mobile)")
    private String width;

    @Option(name = "--define", aliases = "-D", metaVar = "<key=value or key+=value>", usage = "define parameters for capabilities. (multiple)")
    private String[] define;

    @Option(name = "--rollup", metaVar = "<file>", usage = "define rollup rule by JavaScript. (multiple)")
    private String[] rollup;

    @Option(name = "--cookie-filter", metaVar = "<+RE|-RE>", usage = "filter cookies to log by RE matching the name. (\"+\" is passing, \"-\" is ignoring)")
    private String cookieFilter;

    @Option(name = "--command-factory", metaVar = "<FQCN>", usage = "register user defined command factory. (See Note *3)")
    private String commandFactory;

    @Option(name = "--no-exit", usage = "don't call System.exit at end.")
    private Boolean noExit;

    @Option(name = "--strict-exit-code", usage = "return strict exit code, reflected by selenese command results at end. (See Note *4)")
    private Boolean strictExitCode;

    @Option(name = "--help", aliases = "-h", usage = "show this message.")
    private Boolean help;

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

    @Override
    public String getDriver() {
        return driver != null ? driver : (parentOptions != null ? parentOptions.getDriver() : null);
    }

    public void setDriver(String driver) {
        this.driver = driver;
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
    public String getPhantomjs() {
        return phantomjs != null ? phantomjs : (parentOptions != null ? parentOptions.getPhantomjs() : null);
    }

    public void setPhantomjs(String phantomjs) {
        this.phantomjs = phantomjs;
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
    public String[] getDefine() {
        return define != null ? define : (parentOptions != null ? parentOptions.getDefine() : null);
    }

    public void addDefine(String defineItem) {
        this.define = ArrayUtils.add(this.define, defineItem);
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
    public boolean isHelp() {
        return help != null ? help : (parentOptions != null ? parentOptions.isHelp() : false);
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

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
                    if (StringUtils.isNotEmpty(value))
                        setOrAddOptionValue(currentKey, value);
                } else if (currentKey != null) {
                    String value = matcher.group(3);
                    if (StringUtils.isNotEmpty(value))
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

    @Override
    public boolean hasOption(String opt) {
        Object value = get(opt);
        return (value instanceof Boolean) ? (boolean) value : value != null;
    }

    @Override
    public String getOptionValue(String opt) {
        return get(opt);
    }

    @Override
    public String getOptionValue(String opt, String defaultValue) {
        return get(opt, defaultValue);
    }

    @Override
    public String[] getOptionValues(String opt) {
        return get(opt);
    }

    @Override
    public boolean getOptionValueAsBoolean(String opt) {
        return get(opt);
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
        pw.println(WordUtils.wrap(title + " " + version, width));
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
                    padding = StringUtils.repeat(' ', mark.length());
                    String desc = WordUtils.wrap(m.group(2), width - mark.length(), "\n", false).replace("\n", SystemUtils.LINE_SEPARATOR + padding);
                    pw.print(mark);
                    pw.print(desc);
                    pw.println();
                } else {
                    pw.print(padding);
                    pw.println(WordUtils.wrap(line, width - padding.length(), "\n", false).replace("\n", SystemUtils.LINE_SEPARATOR + padding));
                }
            }
        }
        pw.flush();
    }
}
