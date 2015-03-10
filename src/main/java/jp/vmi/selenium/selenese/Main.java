package jp.vmi.selenium.selenese;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.config.IConfig;
import jp.vmi.selenium.selenese.config.SeleneseRunnerOptions;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.CookieFilter.FilterType;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static jp.vmi.selenium.selenese.config.SeleneseRunnerOptions.*;

/**
 * Provide command line interface.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String PROG_TITLE = "Selenese Runner";

    private boolean noExit = false;
    private boolean exitStrictly = false;
    private Integer exitCode = null;

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

    private void help(String... msgs) {
        String progName = System.getenv("PROG_NAME");
        if (StringUtils.isBlank(progName))
            progName = "java -jar selenese-runner.jar";
        new SeleneseRunnerOptions().showHelp(new PrintWriter(System.out), PROG_TITLE, getVersion(), progName, msgs);
        exit(1);
    }

    /**
     * Start Selenese Runner.
     *
     * @param args command line arguments.
     */
    public void run(String[] args) {
        int exitCode = 1;
        try {
            IConfig config = new DefaultConfig(args);
            String[] filenames = config.getArgs();
            if (filenames.length == 0)
                help();
            log.info("Start: " + PROG_TITLE + " {}", getVersion());
            Runner runner = new Runner();
            runner.setCommandLineArgs(args);
            setupRunner(runner, config, filenames);
            Result totalResult = runner.run(filenames);
            runner.finish();
            if (exitStrictly)
                exitCode = totalResult.getLevel().strictExitCode;
            else
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
        WebDriverManager manager = WebDriverManager.newInstance();
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
        if (config.hasOption(NO_EXIT))
            noExit = true;
        if (config.hasOption(STRICT_EXIT_CODE))
            exitStrictly = true;
        runner.setPrintStream(System.out);
    }

    protected void exit(int exitCode) {
        this.exitCode = exitCode;
        log.info("Exit code: {}", exitCode);
        WebDriverManager.quitDriversOnAllManagers();
        if (!noExit)
            System.exit(exitCode);
    }

    /**
     * Get exit code.
     *
     * @return exit code, or null if don't end.
     */
    public Integer getExitCode() {
        return exitCode;
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
