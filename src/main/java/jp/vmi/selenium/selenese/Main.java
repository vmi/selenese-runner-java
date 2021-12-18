package jp.vmi.selenium.selenese;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jp.vmi.selenium.runner.converter.Converter;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.config.DefaultConfig;
import jp.vmi.selenium.selenese.config.IConfig;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.CookieFilter.FilterType;
import jp.vmi.selenium.selenese.log.LogFilter;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Result.Level;
import jp.vmi.selenium.selenese.utils.CommandDumper;
import jp.vmi.selenium.selenese.utils.LangUtils;
import jp.vmi.selenium.selenese.utils.LoggerUtils;
import jp.vmi.selenium.webdriver.DriverOptions;
import jp.vmi.selenium.webdriver.DriverOptions.DriverOption;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static jp.vmi.selenium.selenese.config.DefaultConfig.*;

/**
 * Provide command line interface.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final Pattern EXPR_RE = Pattern.compile("\\s*(?<varName>\\w+)\\s*=\\s*(?<jsonValue>.*)");

    /** Program title */
    public static final String PROG_TITLE = "Selenese Runner";

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
    public static String getVersion() {
        try (InputStream is = Main.class.getResourceAsStream("/META-INF/maven/jp.vmi/selenese-runner-java/pom.properties")) {
            if (is != null) {
                Properties prop = new Properties();
                prop.load(is);
                return prop.getProperty("version");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "(missing version information)";
    }

    private void help(String... msgs) {
        String progName = System.getenv("PROG_NAME");
        if (LangUtils.isBlank(progName))
            progName = "java -jar selenese-runner.jar";
        new DefaultConfig().showHelp(new PrintWriter(System.out), PROG_TITLE, getVersion(), progName, msgs);
        noExit = false;
        exit(Level.USAGE);
    }

    /**
     * Start Selenese Runner.
     *
     * @param args command line arguments.
     */
    public void run(String[] args) {
        Level exitLevel = Level.UNEXECUTED;
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
            exitLevel = totalResult.getLevel();
        } catch (IllegalArgumentException e) {
            help("Error: " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
            exitLevel = Level.FATAL;
        }
        exit(exitLevel);
    }

    /**
     * Setup Runner by configuration.
     *
     * @param runner Runner object.
     * @param config configuration.
     * @param filenames filenames of test-suites/test-cases.
     */
    public void setupRunner(Runner runner, IConfig config, String... filenames) {
        if (config.getMaxTime() != null) {
            long maxTime = NumberUtils.toLong(config.getMaxTime(), 0);
            if (maxTime <= 0)
                throw new IllegalArgumentException("Invalid max time value. (" + config.getMaxTime() + ")");
            runner.setupMaxTimeTimer(maxTime * 1000);
        }
        String driverName = config.getDriver();
        DriverOptions driverOptions = new DriverOptions(config);
        if (driverName == null) {
            if (driverOptions.has(DriverOption.FIREFOX) || driverOptions.has(DriverOption.GECKODRIVER))
                driverName = WebDriverManager.FIREFOX;
            else if (driverOptions.has(DriverOption.CHROMEDRIVER))
                driverName = WebDriverManager.CHROME;
            else if (driverOptions.has(DriverOption.IEDRIVER))
                driverName = WebDriverManager.IE;
            else if (driverOptions.has(DriverOption.EDGEDRIVER))
                driverName = WebDriverManager.EDGE;
        }
        WebDriverManager manager = WebDriverManager.newInstance();
        manager.setWebDriverFactory(driverName);
        manager.setDriverOptions(driverOptions);
        if (config.getCommandFactory() != null) {
            String factoryName = config.getCommandFactory();
            ICommandFactory factory;
            try {
                Class<?> factoryClass = Class.forName(factoryName);
                factory = (ICommandFactory) factoryClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalArgumentException("invalid user defined command factory: " + factoryName);
            }
            runner.getCommandFactory().registerCommandFactory(factory);
            log.info("Registered: {}", factoryName);
        }
        runner.setDriver(manager.get());
        runner.setWebDriverPreparator(manager);
        if (config.isHighlight())
            runner.setHighlight(true);
        if (config.isInteractive())
            runner.getInteractiveModeHandler().setEnabled(true);
        if (config.getScreenshotDir() != null)
            runner.setScreenshotDir(config.getScreenshotDir());
        if (config.getScreenshotAll() != null)
            runner.setScreenshotAllDir(config.getScreenshotAll());
        if (config.getScreenshotOnFail() != null)
            runner.setScreenshotOnFailDir(config.getScreenshotOnFail());
        if (config.getBaseurl() != null)
            runner.setOverridingBaseURL(config.getBaseurl());
        if (config.isIgnoreScreenshotCommand())
            runner.setIgnoredScreenshotCommand(true);
        if (config.getVar() != null) {
            Gson gson = new Gson();
            VarsMap varsMap = runner.getVarsMap();
            for (String expr : config.getVar()) {
                Matcher matcher = EXPR_RE.matcher(expr);
                if (!matcher.matches())
                    throw new IllegalArgumentException("invalid var option format: " + expr);
                String name = matcher.group("varName");
                Object value;
                try {
                    value = gson.fromJson(matcher.group("jsonValue"), Object.class);
                } catch (JsonSyntaxException e) {
                    throw new IllegalArgumentException("JSON syntax error: " + expr);
                }
                varsMap.put(name, value);
            }
        }
        if (config.getRollup() != null) {
            String[] rollups = config.getRollup();
            for (String rollup : rollups)
                runner.getRollupRules().load(rollup);
        }
        if (config.getLogFilter() != null) {
            LogFilter.parse(runner.getLogFilter(), config.getLogFilter());
        }
        if (config.getCookieFilter() != null) {
            String cookieFilter = config.getCookieFilter();
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
        if (config.getXmlResult() != null)
            runner.setJUnitResultDir(config.getXmlResult());
        if (config.getHtmlResult() != null)
            runner.setHtmlResultDir(config.getHtmlResult());
        int timeout = NumberUtils.toInt(config.getTimeout(), DEFAULT_TIMEOUT_MILLISEC_N);
        if (timeout <= 0)
            throw new IllegalArgumentException("Invalid timeout value. (" + config.getTimeout() + ")");
        runner.setTimeout(timeout);
        int maxRetries = NumberUtils.toInt(config.getMaxRetries(), DEFAULT_MAX_RETRIES);
        if ((maxRetries < 0) || (maxRetries > 10))
            throw new IllegalArgumentException("Invalid value: 0 <= maxRetries <= 10. (" + config.getMaxRetries() + ")");
        runner.setMaxRetries(maxRetries);
        int speed = NumberUtils.toInt(config.getSetSpeed(), 0);
        if (speed < 0)
            throw new IllegalArgumentException("Invalid speed value. (" + config.getSetSpeed() + ")");
        runner.setInitialSpeed(speed);
        if (config.isNoExit())
            noExit = true;
        if (config.isStrictExitCode())
            exitStrictly = true;
        int sstimeout = NumberUtils.toInt(config.getScreenshotScrollTimeout(), 100);
        if (sstimeout < 0)
            throw new IllegalArgumentException("Invalid screenshot scroll timeout value. (" + config.getScreenshotScrollTimeout() + ")");
        runner.setScreenshotScrollTimeout(sstimeout);
        if (config.isNoReplaceAlertMethod())
            runner.setReplaceAlertMethod(false);
        runner.setPrintStream(System.out);
    }

    private void dumpThreads() {
        log.trace("Dump threads:");
        Thread.getAllStackTraces().entrySet().stream()
            .sorted((a, b) -> (int) (a.getKey().getId() - b.getKey().getId()))
            .forEach(entry -> {
                Thread t = entry.getKey();
                String daemon = t.isDaemon() ? " (daemon)" : "";
                log.trace("[{}] {}{}", t.getId(), t.getName(), daemon);
                for (StackTraceElement s : entry.getValue()) {
                    int line = s.getLineNumber();
                    String lns = line > 0 ? ":" + line : "";
                    log.trace("  {}#{}{}", s.getClassName(), s.getMethodName(), lns);
                }
                log.trace("--------");
            });
    }

    protected void exit(Level exitLevel) {
        exitCode = exitStrictly ? exitLevel.strictExitCode : exitLevel.exitCode;
        WebDriverManager.quitDriversOnAllManagers();
        log.info("Exit code: {} ({})", exitCode, exitLevel);
        if (!noExit)
            System.exit(exitCode);
        dumpThreads();
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
        Main main = new Main();
        if (args.length == 0)
            main.help();
        switch (args[0]) {
        case "convert":
            Converter.main(Arrays.copyOfRange(args, 1, args.length));
            break;
        case "commands":
            CommandDumper.main(Arrays.copyOfRange(args, 1, args.length));
            break;
        default:
            main.run(args);
            break;
        }
    }
}
