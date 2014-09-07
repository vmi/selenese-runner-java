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
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.CookieFilter.FilterType;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * Provide command line interface.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final int HELP_WIDTH = 78;

    private static final String PROG_TITLE = "Selenese Runner";

    private static final String HEADER = "Selenese script interpreter implemented by Java.";

    private static final String FOOTER = "*note: If you want to use basic and/or proxy authentication on Firefox, "
        + "then create new profile, "
        + "install AutoAuth plugin, "
        + "configure all settings, "
        + "access test site with the profile, "
        + "and specify the profile by --profile option.";

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
        options.addOption(OptionBuilder
            .withLongOpt("driver")
            .hasArg()
            .withArgName("driver")
            .withDescription(
                "firefox (default) | chrome | ie | safari | htmlunit | phantomjs | remote | appium | FQCN-of-WebDriverFactory")
            .create('d'));
        options.addOption(OptionBuilder.withLongOpt("profile")
            .hasArg().withArgName("name")
            .withDescription("profile name (Firefox only)")
            .create('p'));
        options.addOption(OptionBuilder.withLongOpt("profile-dir")
            .hasArg().withArgName("dir")
            .withDescription("profile directory (Firefox only)")
            .create('P'));
        options.addOption(OptionBuilder.withLongOpt("proxy")
            .hasArg().withArgName("proxy")
            .withDescription("proxy host and port (HOST:PORT) (excepting IE)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("proxy-user")
            .hasArg().withArgName("user")
            .withDescription("proxy username (HtmlUnit only *)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("proxy-password")
            .hasArg().withArgName("password")
            .withDescription("proxy password (HtmlUnit only *)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("no-proxy")
            .hasArg().withArgName("no-proxy")
            .withDescription("no-proxy hosts")
            .create());
        options.addOption(OptionBuilder.withLongOpt("remote-url")
            .hasArg().withArgName("url")
            .withDescription("Remote test runner URL (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("remote-platform")
            .hasArg().withArgName("platform")
            .withDescription("Desired remote platform (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("remote-browser")
            .hasArg().withArgName("browser")
            .withDescription("Desired remote browser (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("remote-version")
            .hasArg().withArgName("browser-version")
            .withDescription("Desired remote browser version (Remote only)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("highlight")
            .withDescription("highlight locator always.")
            .create('H'));
        options.addOption(OptionBuilder.withLongOpt("screenshot-dir")
            .hasArg().withArgName("dir")
            .withDescription("override captureEntirePageScreenshot directory.")
            .create('s'));
        options.addOption(OptionBuilder.withLongOpt("screenshot-all")
            .hasArg().withArgName("dir")
            .withDescription("take screenshot at all commands to specified directory.")
            .create('S'));
        options.addOption(OptionBuilder.withLongOpt("screenshot-on-fail")
            .hasArg().withArgName("dir")
            .withDescription("take screenshot on fail commands to specified directory.")
            .create());
        options.addOption(OptionBuilder.withLongOpt("ignore-screenshot-command")
            .withDescription("ignore captureEntirePageScreenshot command.")
            .create());
        options.addOption(OptionBuilder.withLongOpt("baseurl")
            .hasArg().withArgName("baseURL")
            .withDescription("override base URL set in selenese.")
            .create('b'));
        options.addOption(OptionBuilder.withLongOpt("firefox")
            .hasArg().withArgName("path")
            .withDescription("path to 'firefox' binary. (implies '--driver firefox')")
            .create());
        options.addOption(OptionBuilder.withLongOpt("chromedriver")
            .hasArg().withArgName("path")
            .withDescription("path to 'chromedriver' binary. (implies '--driver chrome')")
            .create());
        options.addOption(OptionBuilder.withLongOpt("iedriver")
            .hasArg().withArgName("path")
            .withDescription("path to 'IEDriverServer' binary. (implies '--driver ie')")
            .create());
        options.addOption(OptionBuilder.withLongOpt("phantomjs")
            .hasArg().withArgName("path")
            .withDescription("path to 'phantomjs' binary. (implies '--driver phantomjs')")
            .create());
        options.addOption(OptionBuilder.withLongOpt("xml-result")
            .hasArg().withArgName("dir")
            .withDescription("output XML JUnit results to specified directory.")
            .create());
        options.addOption(OptionBuilder.withLongOpt("html-result")
            .hasArg().withArgName("dir")
            .withDescription("output HTML results to specified directory.")
            .create());
        options.addOption(OptionBuilder.withLongOpt("timeout")
            .hasArg().withArgName("timeout")
            .withDescription("set timeout (ms) for waiting. (default: " + DEFAULT_TIMEOUT_MILLISEC + " ms)")
            .create('t'));
        options.addOption(OptionBuilder.withLongOpt("set-speed")
            .hasArg().withArgName("speed")
            .withDescription("same as executing setSpeed(ms) command first.")
            .create());
        options.addOption(OptionBuilder.withLongOpt("height")
            .hasArg().withArgName("height")
            .withDescription("set initial height. (excluding mobile)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("width")
            .hasArg().withArgName("width")
            .withDescription("set initial width. (excluding mobile)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("define")
            .hasArg().withArgName("key=value or key+=value")
            .withDescription("define parameters for capabilities. (multiple)")
            .create('D'));
        options.addOption(OptionBuilder.withLongOpt("rollup")
            .hasArg().withArgName("file")
            .withDescription("define rollup rule by JavaScript. (multiple)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("cookie-filter")
            .hasArg().withArgName("+RE|-RE")
            .withDescription("filter cookies to log by RE matching the name. (\"+\" is passing, \"-\" is ignoring)")
            .create());
        options.addOption(OptionBuilder.withLongOpt("help")
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
            String driverName = cli.getOptionValue("driver");
            DriverOptions driverOptions = new DriverOptions(cli);
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
            Runner runner = new Runner();
            runner.setDriver(manager.get());
            runner.setWebDriverPreparator(manager);
            runner.setHighlight(cli.hasOption("highlight"));
            runner.setScreenshotDir(cli.getOptionValue("screenshot-dir"));
            runner.setScreenshotAllDir(cli.getOptionValue("screenshot-all"));
            runner.setScreenshotOnFailDir(cli.getOptionValue("screenshot-on-fail"));
            runner.setOverridingBaseURL(cli.getOptionValue("baseurl"));
            runner.setIgnoredScreenshotCommand(cli.hasOption("ignore-screenshot-command"));
            if (cli.hasOption("rollup")) {
                String[] rollups = cli.getOptionValues("rollup");
                for (String rollup : rollups)
                    runner.getRollupRules().load(rollup);
            }
            if (cli.hasOption("cookie-filter")) {
                String cookieFilter = cli.getOptionValue("cookie-filter");
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
            runner.setJUnitResultDir(cli.getOptionValue("xml-result"));
            runner.setHtmlResultDir(cli.getOptionValue("html-result"));
            int timeout = NumberUtils.toInt(cli.getOptionValue("timeout", DEFAULT_TIMEOUT_MILLISEC));
            if (timeout <= 0)
                throw new IllegalArgumentException("Invalid timeout value. (" + cli.getOptionValue("timeout") + ")");
            runner.setTimeout(timeout);
            int speed = NumberUtils.toInt(cli.getOptionValue("set-speed", "0"));
            if (speed < 0)
                throw new IllegalArgumentException("Invalid speed value. (" + cli.getOptionValue("set-speed") + ")");
            runner.setInitialSpeed(speed);
            runner.setPrintStream(System.out);
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
