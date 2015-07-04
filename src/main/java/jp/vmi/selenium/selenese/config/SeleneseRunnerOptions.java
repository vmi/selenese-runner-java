package jp.vmi.selenium.selenese.config;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.result.Result.Level;

import static jp.vmi.selenium.selenese.result.Result.Level.*;
import static org.apache.commons.lang3.SystemUtils.*;

/**
 * Selenese Runner Options.
 */
@SuppressWarnings("javadoc")
public class SeleneseRunnerOptions extends Options {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(SeleneseRunnerOptions.class);

    private static final int HELP_WIDTH = 78;

    // List of option name.
    public static final String CONFIG = "config";
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

    // default values.
    public static final String DEFAULT_TIMEOUT_MILLISEC = "30000";

    // parts of help message.
    private static final String HEADER = "Selenese script interpreter implemented by Java.";

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
        "*3 Use \"java -cp ..." + PATH_SEPARATOR + "selenese-runner.jar Main --command-factory ...\". " + LINE_SEPARATOR
            + "Because \"java\" command ignores all class path settings, when using \"-jar\" option.",
        "",
        "*4 The list of strict exit code is follows:" + LINE_SEPARATOR
            + statusListItem(SUCCESS) + LINE_SEPARATOR
            + statusListItem(WARNING) + LINE_SEPARATOR
            + statusListItem(FAILURE) + LINE_SEPARATOR
            + statusListItem(ERROR) + LINE_SEPARATOR
            + statusListItem(UNEXECUTED)
    };

    // org.apache.commons.cli.OptionBuilder is not thread-safe, and will be deprecated on 1.3.
    private static class OptionBuilder {

        private final String longOpt;
        private boolean hasArg = false;
        private String argName = null;
        private String description;

        private OptionBuilder(String longOpt) {
            this.longOpt = longOpt;
        }

        public static OptionBuilder withLongOpt(String longOpt) {
            return new OptionBuilder(longOpt);
        }

        public OptionBuilder hasArg() {
            hasArg = true;
            return this;
        }

        public OptionBuilder withArgName(String argName) {
            this.argName = argName;
            return this;
        }

        public OptionBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Option create(String shortOpt) {
            Option opt = new Option(shortOpt, longOpt, hasArg, description);
            if (argName != null)
                opt.setArgName(argName);
            return opt;
        }

        public Option create(char shortOpt) {
            return create(String.valueOf(shortOpt));
        }

        public Option create() {
            return create(null);
        }
    }

    private int i = 0;

    private final Map<Option, Integer> optionOrder = new HashMap<Option, Integer>();

    /**
     * Constructor.
     */
    public SeleneseRunnerOptions() {
        addOption(OptionBuilder.withLongOpt(CONFIG)
            .hasArg().withArgName("file")
            .withDescription("load option information from file.")
            .create("c"));
        addOption(OptionBuilder.withLongOpt(DRIVER)
            .hasArg().withArgName("driver")
            .withDescription(
                "firefox (default) | chrome | ie | safari | htmlunit | phantomjs | remote | appium | FQCN-of-WebDriverFactory")
            .create('d'));
        addOption(OptionBuilder.withLongOpt(PROFILE)
            .hasArg().withArgName("name")
            .withDescription("profile name (Firefox only *1)")
            .create('p'));
        addOption(OptionBuilder.withLongOpt(PROFILE_DIR)
            .hasArg().withArgName("dir")
            .withDescription("profile directory (Firefox only *1)")
            .create('P'));
        addOption(OptionBuilder.withLongOpt(CHROME_EXPERIMENTAL_OPTIONS)
            .hasArg().withArgName("file")
            .withDescription("path to json file specify experimental options for chrome (Chrome only *1)")
            .create());
        addOption(OptionBuilder.withLongOpt(CHROME_EXTENSION)
            .hasArg().withArgName("file")
            .withDescription("chrome extension file (Chrome only *1)")
            .create());
        addOption(OptionBuilder.withLongOpt(PROXY)
            .hasArg().withArgName("proxy")
            .withDescription("proxy host and port (HOST:PORT) (excepting IE)")
            .create());
        addOption(OptionBuilder.withLongOpt(PROXY_USER)
            .hasArg().withArgName("user")
            .withDescription("proxy username (HtmlUnit only *2)")
            .create());
        addOption(OptionBuilder.withLongOpt(PROXY_PASSWORD)
            .hasArg().withArgName("password")
            .withDescription("proxy password (HtmlUnit only *2)")
            .create());
        addOption(OptionBuilder.withLongOpt(NO_PROXY)
            .hasArg().withArgName("hosts")
            .withDescription("no-proxy hosts")
            .create());
        addOption(OptionBuilder.withLongOpt(CLI_ARGS)
            .hasArg().withArgName("arg")
            .withDescription("add command line arguments at starting up driver (multiple)")
            .create());
        addOption(OptionBuilder.withLongOpt(REMOTE_URL)
            .hasArg().withArgName("url")
            .withDescription("Remote test runner URL (Remote only)")
            .create());
        addOption(OptionBuilder.withLongOpt(REMOTE_PLATFORM)
            .hasArg().withArgName("platform")
            .withDescription("Desired remote platform (Remote only)")
            .create());
        addOption(OptionBuilder.withLongOpt(REMOTE_BROWSER)
            .hasArg().withArgName("browser")
            .withDescription("Desired remote browser (Remote only)")
            .create());
        addOption(OptionBuilder.withLongOpt(REMOTE_VERSION)
            .hasArg().withArgName("browser-version")
            .withDescription("Desired remote browser version (Remote only)")
            .create());
        addOption(OptionBuilder.withLongOpt(HIGHLIGHT)
            .withDescription("highlight locator always.")
            .create('H'));
        addOption(OptionBuilder.withLongOpt(SCREENSHOT_DIR)
            .hasArg().withArgName("dir")
            .withDescription("override captureEntirePageScreenshot directory.")
            .create('s'));
        addOption(OptionBuilder.withLongOpt(SCREENSHOT_ALL)
            .hasArg().withArgName("dir")
            .withDescription("take screenshot at all commands to specified directory.")
            .create('S'));
        addOption(OptionBuilder.withLongOpt(SCREENSHOT_ON_FAIL)
            .hasArg().withArgName("dir")
            .withDescription("take screenshot on fail commands to specified directory.")
            .create());
        addOption(OptionBuilder.withLongOpt(IGNORE_SCREENSHOT_COMMAND)
            .withDescription("ignore captureEntirePageScreenshot command.")
            .create());
        addOption(OptionBuilder.withLongOpt(BASEURL)
            .hasArg().withArgName("baseURL")
            .withDescription("override base URL set in selenese.")
            .create('b'));
        addOption(OptionBuilder.withLongOpt(FIREFOX)
            .hasArg().withArgName("path")
            .withDescription("path to 'firefox' binary. (implies '--driver firefox')")
            .create());
        addOption(OptionBuilder.withLongOpt(CHROMEDRIVER)
            .hasArg().withArgName("path")
            .withDescription("path to 'chromedriver' binary. (implies '--driver chrome')")
            .create());
        addOption(OptionBuilder.withLongOpt(IEDRIVER)
            .hasArg().withArgName("path")
            .withDescription("path to 'IEDriverServer' binary. (implies '--driver ie')")
            .create());
        addOption(OptionBuilder.withLongOpt(PHANTOMJS)
            .hasArg().withArgName("path")
            .withDescription("path to 'phantomjs' binary. (implies '--driver phantomjs')")
            .create());
        addOption(OptionBuilder.withLongOpt(XML_RESULT)
            .hasArg().withArgName("dir")
            .withDescription("output XML JUnit results to specified directory.")
            .create());
        addOption(OptionBuilder.withLongOpt(HTML_RESULT)
            .hasArg().withArgName("dir")
            .withDescription("output HTML results to specified directory.")
            .create());
        addOption(OptionBuilder.withLongOpt(TIMEOUT)
            .hasArg().withArgName("timeout")
            .withDescription("set timeout (ms) for waiting. (default: " + DEFAULT_TIMEOUT_MILLISEC + " ms)")
            .create('t'));
        addOption(OptionBuilder.withLongOpt(SET_SPEED)
            .hasArg().withArgName("speed")
            .withDescription("same as executing setSpeed(ms) command first.")
            .create());
        addOption(OptionBuilder.withLongOpt(HEIGHT)
            .hasArg().withArgName("height")
            .withDescription("set initial height. (excluding mobile)")
            .create());
        addOption(OptionBuilder.withLongOpt(WIDTH)
            .hasArg().withArgName("width")
            .withDescription("set initial width. (excluding mobile)")
            .create());
        addOption(OptionBuilder.withLongOpt(DEFINE)
            .hasArg().withArgName("key=value or key+=value")
            .withDescription("define parameters for capabilities. (multiple)")
            .create('D'));
        addOption(OptionBuilder.withLongOpt(ROLLUP)
            .hasArg().withArgName("file")
            .withDescription("define rollup rule by JavaScript. (multiple)")
            .create());
        addOption(OptionBuilder.withLongOpt(COOKIE_FILTER)
            .hasArg().withArgName("+RE|-RE")
            .withDescription("filter cookies to log by RE matching the name. (\"+\" is passing, \"-\" is ignoring)")
            .create());
        addOption(OptionBuilder.withLongOpt(COMMAND_FACTORY)
            .hasArg().withArgName("FQCN")
            .withDescription("register user defined command factory. (See Note *3)")
            .create());
        addOption(OptionBuilder.withLongOpt(NO_EXIT)
            .withDescription("don't call System.exit at end.")
            .create());
        addOption(OptionBuilder.withLongOpt(STRICT_EXIT_CODE)
            .withDescription("return strict exit code, reflected by selenese command results at end. (See Note *4)")
            .create());
        addOption(OptionBuilder.withLongOpt(HELP)
            .withDescription("show this message.")
            .create('h'));
    }

    @Override
    public Options addOption(Option opt) {
        optionOrder.put(opt, ++i);
        return super.addOption(opt);
    }

    /**
     * Parse command line arguments.
     *
     * @param args command line arguments.
     * @return parsed command line information.
     * @throws IllegalArgumentException invalid options.
     */
    public CommandLine parseCommandLine(String... args) throws IllegalArgumentException {
        CommandLine cli = null;
        try {
            cli = new DefaultParser().parse(this, args);
            log.debug("Specified options:");
            for (Option opt : cli.getOptions())
                log.debug("[{}]=[{}]", opt.getLongOpt(), opt.getValue());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return cli;
    }

    private int getHelpWidth() {
        String columns = System.getProperty("columns", System.getenv("COLUMNS"));
        if (columns != null && columns.matches("\\d+")) {
            try {
                return Integer.parseInt(columns) - 2;
            } catch (NumberFormatException e) {
                // no operation
            }
        }
        return HELP_WIDTH;
    }

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
        int helpWidth = getHelpWidth();
        HelpFormatter fmt = new HelpFormatter();
        fmt.setOptionComparator(new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return optionOrder.get(o1) - optionOrder.get(o2);
            }
        });
        pw.format("%s %s%n%n" + HEADER + "%n%n", title, version);
        fmt.setSyntaxPrefix("Usage: ");
        fmt.printHelp(pw, helpWidth, cmdName + " <option> ... <test-case|test-suite> ...\n",
            null, this, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        pw.println();
        for (String footer : FOOTER)
            fmt.printWrapped(pw, helpWidth, 3, footer);
        pw.flush();
    }
}
