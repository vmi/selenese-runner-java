package jp.vmi.selenium.selenese;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.config.IConfig;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.CookieFilter.FilterType;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static jp.vmi.selenium.selenese.config.IConfig.*;

/**
 * Provide command line interface.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int HELP_WIDTH = 78;

    private static final String PROG_TITLE = "Selenese Runner";

    private static final String HEADER = "Selenese script interpreter implemented by Java.";

    private static final String FOOTER = "[Note]"
        + SystemUtils.LINE_SEPARATOR
        + "*1 It is available if using \"--driver remote --remote-browser firefox\"."
        + SystemUtils.LINE_SEPARATOR
        + "*2 If you want to use basic and/or proxy authentication on Firefox, "
        + "then create new profile, "
        + "install AutoAuth plugin, "
        + "configure all settings, "
        + "access test site with the profile, "
        + "and specify the profile by --profile option."
        + SystemUtils.LINE_SEPARATOR
        + "*3 Use \"java -cp ..." + SystemUtils.PATH_SEPARATOR + "selenese-runner.jar Main --command-factory ...\". "
        + "Because \"java\" command ignores all class path settings, when using \"-jar\" option.";

    private static final String DEFAULT_TIMEOUT_MILLISEC = "30000";

    private static class SROptions extends Options {
        private static final long serialVersionUID = 1L;

        private int i = 0;

        public final Map<Option, Integer> optionOrder = new HashMap<Option, Integer>();

        @Override
        public Options addOption(Option opt) {
            optionOrder.put(opt, ++i);
            return super.addOption(opt);
        }

        public Comparator<Option> getOptionComparator() {
            return new Comparator<Option>() {
                @Override
                public int compare(Option o1, Option o2) {
                    return optionOrder.get(o1) - optionOrder.get(o2);
                }
            };
        }
    }

    private final SROptions options = new SROptions();

    /**
     * Constructor.
     */
    @SuppressWarnings("static-access")
    public Main() {
        options.addOption(OptionBuilder.withLongOpt(CONFIG)
            .hasArg().withArgName("file")
            .withDescription("load option information from file.")
            .create("c"));
        options.addOption(OptionBuilder.withLongOpt(DRIVER)
            .hasArg().withArgName("driver")
            .withDescription(
                "firefox (default) | chrome | ie | safari | htmlunit | phantomjs | remote | appium | FQCN-of-WebDriverFactory")
            .create('d'));
        options.addOption(OptionBuilder.withLongOpt(PROFILE)
            .hasArg().withArgName("name")
            .withDescription("profile name (Firefox only *1)")
            .create('p'));
        options.addOption(OptionBuilder.withLongOpt(PROFILE_DIR)
            .hasArg().withArgName("dir")
            .withDescription("profile directory (Firefox only *1)")
            .create('P'));
        options.addOption(OptionBuilder.withLongOpt(PROXY)
            .hasArg().withArgName("proxy")
            .withDescription("proxy host and port (HOST:PORT) (excepting IE)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(PROXY_USER)
            .hasArg().withArgName("user")
            .withDescription("proxy username (HtmlUnit only *2)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(PROXY_PASSWORD)
            .hasArg().withArgName("password")
            .withDescription("proxy password (HtmlUnit only *2)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(NO_PROXY)
            .hasArg().withArgName("hosts")
            .withDescription("no-proxy hosts")
            .create());
        options.addOption(OptionBuilder.withLongOpt(CLI_ARGS)
            .hasArg().withArgName("arg")
            .withDescription("add command line arguments at starting up driver (multiple)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(REMOTE_URL)
            .hasArg().withArgName("url")
            .withDescription("Remote test runner URL (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(REMOTE_PLATFORM)
            .hasArg().withArgName("platform")
            .withDescription("Desired remote platform (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(REMOTE_BROWSER)
            .hasArg().withArgName("browser")
            .withDescription("Desired remote browser (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(REMOTE_VERSION)
            .hasArg().withArgName("browser-version")
            .withDescription("Desired remote browser version (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(HIGHLIGHT)
            .withDescription("highlight locator always.")
            .create('H'));
        options.addOption(OptionBuilder.withLongOpt(SCREENSHOT_DIR)
            .hasArg().withArgName("dir")
            .withDescription("override captureEntirePageScreenshot directory.")
            .create('s'));
        options.addOption(OptionBuilder.withLongOpt(SCREENSHOT_ALL)
            .hasArg().withArgName("dir")
            .withDescription("take screenshot at all commands to specified directory.")
            .create('S'));
        options.addOption(OptionBuilder.withLongOpt(SCREENSHOT_ON_FAIL)
            .hasArg().withArgName("dir")
            .withDescription("take screenshot on fail commands to specified directory.")
            .create());
        options.addOption(OptionBuilder.withLongOpt(IGNORE_SCREENSHOT_COMMAND)
            .withDescription("ignore captureEntirePageScreenshot command.")
            .create());
        options.addOption(OptionBuilder.withLongOpt(BASEURL)
            .hasArg().withArgName("baseURL")
            .withDescription("override base URL set in selenese.")
            .create('b'));
        options.addOption(OptionBuilder.withLongOpt(FIREFOX)
            .hasArg().withArgName("path")
            .withDescription("path to 'firefox' binary. (implies '--driver firefox')")
            .create());
        options.addOption(OptionBuilder.withLongOpt(CHROMEDRIVER)
            .hasArg().withArgName("path")
            .withDescription("path to 'chromedriver' binary. (implies '--driver chrome')")
            .create());
        options.addOption(OptionBuilder.withLongOpt(IEDRIVER)
            .hasArg().withArgName("path")
            .withDescription("path to 'IEDriverServer' binary. (implies '--driver ie')")
            .create());
        options.addOption(OptionBuilder.withLongOpt(PHANTOMJS)
            .hasArg().withArgName("path")
            .withDescription("path to 'phantomjs' binary. (implies '--driver phantomjs')")
            .create());
        options.addOption(OptionBuilder.withLongOpt(XML_RESULT)
            .hasArg().withArgName("dir")
            .withDescription("output XML JUnit results to specified directory.")
            .create());
        options.addOption(OptionBuilder.withLongOpt(HTML_RESULT)
            .hasArg().withArgName("dir")
            .withDescription("output HTML results to specified directory.")
            .create());
        options.addOption(OptionBuilder.withLongOpt(TIMEOUT)
            .hasArg().withArgName("timeout")
            .withDescription("set timeout (ms) for waiting. (default: " + DEFAULT_TIMEOUT_MILLISEC + " ms)")
            .create('t'));
        options.addOption(OptionBuilder.withLongOpt(SET_SPEED)
            .hasArg().withArgName("speed")
            .withDescription("same as executing setSpeed(ms) command first.")
            .create());
        options.addOption(OptionBuilder.withLongOpt(HEIGHT)
            .hasArg().withArgName("height")
            .withDescription("set initial height. (excluding mobile)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(WIDTH)
            .hasArg().withArgName("width")
            .withDescription("set initial width. (excluding mobile)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(DEFINE)
            .hasArg().withArgName("key=value or key+=value")
            .withDescription("define parameters for capabilities. (multiple)")
            .create('D'));
        options.addOption(OptionBuilder.withLongOpt(ROLLUP)
            .hasArg().withArgName("file")
            .withDescription("define rollup rule by JavaScript. (multiple)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(COOKIE_FILTER)
            .hasArg().withArgName("+RE|-RE")
            .withDescription("filter cookies to log by RE matching the name. (\"+\" is passing, \"-\" is ignoring)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(COMMAND_FACTORY)
            .hasArg().withArgName("FQCN")
            .withDescription("register user defined command factory. (See Note *3)")
            .create());
        options.addOption(OptionBuilder.withLongOpt(HELP)
            .withDescription("show this message.")
            .create('h'));
    }

    /**
     * Get version of Selenese Runner.
     * <p>
     * This information is provided by maven generated property file.
     * </p>
     * @return version string.
     */
    public String getVersion() {
        InputStream is = getClass().getResourceAsStream("/META-INF/maven/jp.vmi/selenese-runner-java/pom.properties");
        if (is != null) {
            try {
                Properties prop = new Properties();
                prop.load(is);
                return prop.getProperty("version");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return "(missing version information)";
    }

    private int getHelpWidth() {
        String columns = System.getenv("COLUMNS");
        if (columns != null && columns.matches("\\d+")) {
            try {
                return Integer.parseInt(columns) - 2;
            } catch (NumberFormatException e) {
                // no operation
            }
        }
        return HELP_WIDTH;
    }

    private void help(String... msgs) {
        if (msgs.length > 0) {
            for (String msg : msgs)
                System.out.println(msg);
            System.out.println();
        }

        int helpWidth = getHelpWidth();
        String progName = System.getenv("PROG_NAME");
        if (StringUtils.isBlank(progName))
            progName = "java -jar selenese-runner.jar";
        HelpFormatter fmt = new HelpFormatter();
        fmt.setOptionComparator(options.getOptionComparator());
        PrintWriter pw = new PrintWriter(System.out);
        pw.format(PROG_TITLE + " %s%n%n" + HEADER + "%n%n", getVersion());
        fmt.setSyntaxPrefix("Usage: ");
        fmt.printHelp(pw, helpWidth, progName + " <option> ... <test-case|test-suite> ...\n",
            null, options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, null);
        pw.println();
        fmt.printWrapped(pw, helpWidth, FOOTER);
        pw.flush();
        exit(1);
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
            cli = new PosixParser().parse(options, args);
            log.debug("Specified options:");
            for (Option opt : cli.getOptions())
                log.debug("[{}]=[{}]", opt.getLongOpt(), opt.getValue());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        return cli;
    }

    /**
     * Start Selenese Runner.
     *
     * @param args command line arguments.
     */
    public void run(String[] args) {
        int exitCode = 1;
        try {
            CommandLine cli = parseCommandLine(args);
            String[] filenames = cli.getArgs();
            if (filenames.length == 0)
                help();
            log.info("Start: " + PROG_TITLE + " {}", getVersion());
            IConfig config = new DefaultConfig(cli, cli.getOptionValue(CONFIG));
            Runner runner = new Runner();
            runner.setCommandLineArgs(args);
            setupRunner(runner, config, filenames);
            Result totalResult = runner.run(filenames);
            runner.finish();
            exitCode = totalResult.getLevel().exitCode;
        } catch (IllegalArgumentException e) {
            help("Error: " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        exit(exitCode);
    }

    /**
     * Setup Runner by configuration.
     *
     * @param runner Runner object.
     * @param config configuration.
     * @param filenames filenames of test-suites/test-cases.
     */
    public void setupRunner(Runner runner, IConfig config, String... filenames) {
        String driverName = config.getOptionValue(DRIVER);
        DriverOptions driverOptions = new DriverOptions(config);
        if (driverName == null) {
            if (driverOptions.has(DriverOption.FIREFOX))
                driverName = WebDriverManager.FIREFOX;
            else if (driverOptions.has(DriverOption.CHROMEDRIVER))
                driverName = WebDriverManager.CHROME;
            else if (driverOptions.has(DriverOption.IEDRIVER))
                driverName = WebDriverManager.IE;
            else if (driverOptions.has(DriverOption.PHANTOMJS))
                driverName = WebDriverManager.PHANTOMJS;
        }
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(driverName);
        manager.setDriverOptions(driverOptions);
        if (config.hasOption(COMMAND_FACTORY)) {
            String factoryName = config.getOptionValue(COMMAND_FACTORY);
            ICommandFactory factory;
            try {
                Class<?> factoryClass = Class.forName(factoryName);
                factory = (ICommandFactory) factoryClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("invalid user defined command factory: " + factoryName);
            }
            runner.getCommandFactory().registerCommandFactory(factory);
            log.info("Registered: {}", factoryName);
        }
        runner.setDriver(manager.get());
        runner.setWebDriverPreparator(manager);
        if (config.getOptionValueAsBoolean(HIGHLIGHT))
            runner.setHighlight(true);
        if (config.hasOption(SCREENSHOT_DIR))
            runner.setScreenshotDir(config.getOptionValue(SCREENSHOT_DIR));
        if (config.hasOption(SCREENSHOT_ALL))
            runner.setScreenshotAllDir(config.getOptionValue(SCREENSHOT_ALL));
        if (config.hasOption(SCREENSHOT_ON_FAIL))
            runner.setScreenshotOnFailDir(config.getOptionValue(SCREENSHOT_ON_FAIL));
        if (config.hasOption(BASEURL))
            runner.setOverridingBaseURL(config.getOptionValue(BASEURL));
        if (config.getOptionValueAsBoolean(IGNORE_SCREENSHOT_COMMAND))
            runner.setIgnoredScreenshotCommand(true);
        if (config.hasOption(ROLLUP)) {
            String[] rollups = config.getOptionValues(ROLLUP);
            for (String rollup : rollups)
                runner.getRollupRules().load(rollup);
        }
        if (config.hasOption(COOKIE_FILTER)) {
            String cookieFilter = config.getOptionValue(COOKIE_FILTER);
            if (cookieFilter.length() < 2)
                throw new IllegalArgumentException("invalid cookie filter format: " + cookieFilter);
            FilterType filterType;
            switch (cookieFilter.charAt(0)) {
            case '+':
                filterType = FilterType.PASS;
                break;
            case '-':
                filterType = FilterType.SKIP;
                break;
            default:
                throw new IllegalArgumentException("invalid cookie filter format: " + cookieFilter);
            }
            String pattern = cookieFilter.substring(1);
            runner.setCookieFilter(new CookieFilter(filterType, pattern));
        }
        if (config.hasOption(XML_RESULT))
            runner.setJUnitResultDir(config.getOptionValue(XML_RESULT));
        if (config.hasOption(HTML_RESULT))
            runner.setHtmlResultDir(config.getOptionValue(HTML_RESULT));
        int timeout = NumberUtils.toInt(config.getOptionValue(TIMEOUT, DEFAULT_TIMEOUT_MILLISEC));
        if (timeout <= 0)
            throw new IllegalArgumentException("Invalid timeout value. (" + config.getOptionValue(TIMEOUT) + ")");
        runner.setTimeout(timeout);
        int speed = NumberUtils.toInt(config.getOptionValue(SET_SPEED, "0"));
        if (speed < 0)
            throw new IllegalArgumentException("Invalid speed value. (" + config.getOptionValue(SET_SPEED) + ")");
        runner.setInitialSpeed(speed);
        runner.setPrintStream(System.out);
    }

    protected void exit(int exitCode) {
        System.exit(exitCode);
    }

    /**
     * Selenese Runner main.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        LoggerUtils.initLogger();
        new Main().run(args);
    }
}
