package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.html.result.HtmlResult;
import jp.vmi.html.result.HtmlResultHolder;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.junit.result.JUnitResultHolder;
import jp.vmi.selenium.rollup.RollupRules;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.CommandListIterator;
import jp.vmi.selenium.selenese.highlight.HighlightHandler;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;
import jp.vmi.selenium.selenese.highlight.HighlightStyleBackup;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.log.CookieFilter;
import jp.vmi.selenium.selenese.log.PageInformation;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;
import jp.vmi.selenium.selenese.utils.PathUtils;
import jp.vmi.selenium.webdriver.WebDriverPreparator;

import static jp.vmi.selenium.selenese.result.Unexecuted.*;
import static org.openqa.selenium.remote.CapabilityType.*;

/**
 * Provide Java API to run Selenese script.
 */
public class Runner implements Context, ScreenshotHandler, HighlightHandler, JUnitResultHolder, HtmlResultHolder {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private static final FastDateFormat FILE_DATE_TIME = FastDateFormat.getInstance("yyyyMMdd_HHmmssSSS");

    private static PrintStream DEFAULT_PRINT_STREAM = new PrintStream(new NullOutputStream());

    private PrintStream ps;
    private WebDriver driver = null;
    private WebDriverPreparator preparator = null;
    private String overridingBaseURL = null;
    private String initialWindowHandle = null;
    private String screenshotDir = null;
    private String screenshotAllDir = null;
    private String screenshotOnFailDir = null;
    private boolean isIgnoredScreenshotCommand = false;
    private boolean isHighlight = false;
    private int timeout = 30 * 1000; /* ms */
    private long initialSpeed = 0; /* ms */
    private long speed = 0; /* ms */

    private final Eval eval;
    private final SubCommandMap subCommandMap;
    private final WebDriverElementFinder elementFinder;
    private final CommandFactory commandFactory;
    private TestCase currentTestCase = null;
    private final Deque<CommandListIterator> commandListIteratorStack = new ArrayDeque<>();
    private VarsMap varsMap = new VarsMap();
    private final CollectionMap collectionMap = new CollectionMap();
    private RollupRules rollupRules = null; // lazy initialization
    private final Deque<HighlightStyleBackup> styleBackups;

    private PageInformation latestPageInformation = PageInformation.EMPTY;
    private CookieFilter cookieFilter = CookieFilter.ALL_PASS;

    private final JUnitResult jUnitResult = new JUnitResult();
    private final HtmlResult htmlResult = new HtmlResult();

    /**
     * Constructor.
     */
    public Runner() {
        this.ps = DEFAULT_PRINT_STREAM;
        this.eval = new Eval(this);
        this.elementFinder = new WebDriverElementFinder();
        this.subCommandMap = new SubCommandMap(this);
        this.commandFactory = new CommandFactory(this);
        this.varsMap = new VarsMap();
        this.styleBackups = new ArrayDeque<>();
    }

    /**
     * Set command line arguments.
     *
     * @param args command line arguments.
     */
    public void setCommandLineArgs(String[] args) {
        jUnitResult.setCommandLineArgs(args);
        htmlResult.setCommandLineArgs(args);
    }

    @Override
    public TestCase getCurrentTestCase() {
        return currentTestCase;
    }

    @Override
    public void setCurrentTestCase(TestCase currentTestCase) {
        this.currentTestCase = currentTestCase;
    }

    /**
     * Set PrintStream for logging.
     *
     * @param ps PrintStream for logging.
     */
    public void setPrintStream(PrintStream ps) {
        this.ps = ps;
    }

    @Override
    public PrintStream getPrintStream() {
        return ps;
    }

    private TakesScreenshot getTakesScreenshot() {
        if (driver instanceof TakesScreenshot) {
            return (TakesScreenshot) driver;
        } else if (driver instanceof RemoteWebDriver && ((HasCapabilities) driver).getCapabilities().is(TAKES_SCREENSHOT)) {
            return (TakesScreenshot) new Augmenter().augment(driver);
        } else {
            return null;
        }
    }

    private String takeScreenshot(TakesScreenshot tss, File file) throws WebDriverException {
        file = file.getAbsoluteFile();
        try {
            // cf. http://prospire-developers.blogspot.jp/2013/12/selenium-webdriver-tips.html (Japanese)
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            // some times switching to default context throws exceptions like:
            // Method threw 'org.openqa.selenium.UnhandledAlertException' exception.
        }
        File tmp = tss.getScreenshotAs(OutputType.FILE);
        try {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
                log.info("Make the directory for screenshot: {}", dir);
            }
            FileUtils.moveFile(tmp, file);
        } catch (IOException e) {
            throw new RuntimeException("failed to rename captured screenshot image: " + file, e);
        }
        String path = file.getPath();
        log.info("- captured screenshot: {}", path);
        currentTestCase.getLogRecorder().info("[[ATTACHMENT|" + path + "]]");
        return path;
    }

    @Override
    public String takeScreenshot(String filename) throws WebDriverException, UnsupportedOperationException {
        TakesScreenshot tss = getTakesScreenshot();
        if (tss == null)
            throw new UnsupportedOperationException("webdriver does not support capturing screenshot.");
        File file = new File(PathUtils.normalize(filename));
        if (screenshotDir != null)
            file = new File(screenshotDir, file.getName());
        return takeScreenshot(tss, file);
    }

    @Override
    public String takeScreenshotAll(String prefix, int index) {
        if (screenshotAllDir == null)
            return null;
        TakesScreenshot tss = getTakesScreenshot();
        if (tss == null)
            return null;
        String filename = String.format("%s_%s_%d.png", prefix, FILE_DATE_TIME.format(Calendar.getInstance()), index);
        try {
            File file = new File(screenshotAllDir, filename);
            return takeScreenshot(tss, file);
        } catch (WebDriverException e) {
            log.warn("- failed to capture screenshot: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    @Override
    public String takeScreenshotOnFail(String prefix, int index) {
        if (screenshotOnFailDir == null)
            return null;
        TakesScreenshot tss = getTakesScreenshot();
        if (tss == null)
            return null;
        String filename = String.format("%s_%s_%d_fail.png", prefix, FILE_DATE_TIME.format(Calendar.getInstance()), index);
        try {
            File file = new File(screenshotOnFailDir, filename);
            return takeScreenshot(tss, file);
        } catch (WebDriverException e) {
            log.warn("- failed to capture screenshot: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * Get WebDriver.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @return WebDriver.
     */
    @Deprecated
    public WebDriver getDriver() {
        return getWrappedDriver();
    }

    @Override
    public WebDriver getWrappedDriver() {
        return driver;
    }

    @Override
    public String getInitialWindowHandle() {
        return initialWindowHandle;
    }

    /**
     * Set WebDriver.
     *
     * @param driver WebDriver.
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
        this.initialWindowHandle = driver.getWindowHandle();
    }

    @Override
    public void prepareWebDriver() {
        if (preparator == null)
            return;
        setDriver(preparator.get());
    }

    /**
     * Set WebDriverPreparator.
     *
     * @param preparator WebDriverPreparator.
     */
    public void setWebDriverPreparator(WebDriverPreparator preparator) {
        this.preparator = preparator;
    }

    private static void mkdirsForScreenshot(String dirStr, String msg) {
        if (dirStr == null)
            return;
        File dir = new File(dirStr);
        if (dir.exists()) {
            if (dir.isDirectory())
                return;
            else
                throw new IllegalArgumentException(dirStr + " is not directory.");
        }
        dir.mkdirs();
        log.info("Make the directory for {}: {}", msg, dirStr);
    }

    /**
     * Set directory for storing screenshots.
     *
     * @param screenshotDir directory.
     * @exception IllegalArgumentException throws if screenshotDir is not directory.
     */
    public void setScreenshotDir(String screenshotDir) throws IllegalArgumentException {
        mkdirsForScreenshot(screenshotDir, "screenshot");
        this.screenshotDir = screenshotDir;
        log.info("Screenshot directory: {}", StringUtils.defaultString(screenshotDir, "-"));
    }

    /**
     * Set directory for storing screenshots at all commands.
     *
     * @param screenshotAllDir directory.
     * @exception IllegalArgumentException throws if screenshotAllDir is not directory.
     */
    public void setScreenshotAllDir(String screenshotAllDir) throws IllegalArgumentException {
        mkdirsForScreenshot(screenshotAllDir, "screenshot-all");
        this.screenshotAllDir = screenshotAllDir;
        log.info("Screenshot for all commands directory: {}", StringUtils.defaultString(screenshotAllDir, "-"));
    }

    /**
     * Set directory for storing screenshot on fail.
     *
     * @param screenshotOnFailDir directory.
     */
    public void setScreenshotOnFailDir(String screenshotOnFailDir) {
        mkdirsForScreenshot(screenshotOnFailDir, "screenshot-on-fail");
        this.screenshotOnFailDir = screenshotOnFailDir;
        log.info("Screenshot on fail directory: {}", StringUtils.defaultString(screenshotOnFailDir, "-"));
    }

    /**
     * Get current base URL.
     *
     * @return base URL.
     */
    @Deprecated
    public String getBaseURL() {
        return getCurrentBaseURL();
    }

    /**
     * Set URL for overriding selenium.base in Selenese script.
     *
     * @param baseURL base URL.
     *
     * @deprecated Replaced by {@link #setOverridingBaseURL(String)}
     */
    @Deprecated
    public void setBaseURL(String baseURL) {
        setOverridingBaseURL(baseURL);
    }

    @Override
    public String getCurrentBaseURL() {
        return StringUtils.defaultIfBlank(overridingBaseURL, currentTestCase.getBaseURL());
    }

    /**
     * Set URL for overriding test-case base URL.
     *
     * @param overridingBaseURL base URL.
     */
    public void setOverridingBaseURL(String overridingBaseURL) {
        this.overridingBaseURL = overridingBaseURL;
        log.info("Override base URL: {}", overridingBaseURL);
    }

    @Override
    public String getOverridingBaseURL() {
        return overridingBaseURL;
    }

    /**
     * Set ignore screenshot command flag.
     *
     * @deprecated use {@link #setIgnoredScreenshotCommand(boolean)}
     *
     * @param isIgnoredScreenshotCommand set true if you want to ignore "captureEntirePageScreenshot"
     */
    @Deprecated
    public void setIgnoreScreenshotCommand(boolean isIgnoredScreenshotCommand) {
        setIgnoredScreenshotCommand(isIgnoredScreenshotCommand);
    }

    /**
     * Set ignore screenshot command flag.
     *
     * @param isIgnoredScreenshotCommand set true if you want to ignore "captureEntirePageScreenshot"
     */
    public void setIgnoredScreenshotCommand(boolean isIgnoredScreenshotCommand) {
        this.isIgnoredScreenshotCommand = isIgnoredScreenshotCommand;
        log.info("Screenshot command: {}", isIgnoredScreenshotCommand ? "ignored" : "enabled");
    }

    /**
     * Get ignore screenshot command flag.
     *
     * @deprecated use {@link #isIgnoredScreenshotCommand()}
     *
     * @return flag to ignore "captureEntirePageScreenshot"
     */
    @Deprecated
    public boolean isIgnoreScreenshotCommand() {
        return isIgnoredScreenshotCommand();
    }

    @Override
    public boolean isIgnoredScreenshotCommand() {
        return isIgnoredScreenshotCommand;
    }

    @Override
    public boolean isHighlight() {
        return isHighlight;
    }

    /**
     * Set locator highlighting.
     *
     * @param isHighlight true if use locator highlighting.
     */
    public void setHighlight(boolean isHighlight) {
        this.isHighlight = isHighlight;
        log.info("Highlight mode: {}", isHighlight ? "enabled" : "disabled");
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
        log.info("Timeout: {} ms", timeout);
    }

    /**
     * Get initial speed at starting test-suite. (ms)
     *
     * @return initial speed.
     */
    public long getInitialSpeed() {
        return initialSpeed;
    }

    /**
     * Set initial speed at starting test-suite. (ms)
     *
     * @param initialSpeed initial speed.
     */
    public void setInitialSpeed(long initialSpeed) {
        this.initialSpeed = initialSpeed;
    }

    @Override
    public void resetSpeed() {
        speed = initialSpeed;
        log.info("Current speed: {} ms/command", speed);
    }

    @Override
    public long getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(long speed) {
        this.speed = speed;
        log.info("Current speed: {} ms/command", speed);
    }

    @Override
    public void waitSpeed() {
        if (speed > 0) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                // ignore it.
            }
        }
    }

    @Override
    public SubCommandMap getSubCommandMap() {
        return subCommandMap;
    }

    @Override
    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    @Override
    public CommandListIterator getCommandListIterator() {
        return commandListIteratorStack.peekFirst();
    }

    @Override
    public void pushCommandListIterator(CommandListIterator commandListIterator) {
        commandListIteratorStack.push(commandListIterator);
    }

    @Override
    public void popCommandListIterator() {
        commandListIteratorStack.pop();
    }

    @Override
    public VarsMap getVarsMap() {
        return varsMap;
    }

    /**
     * Set variables map used for this session.
     *
     * @param varsMap the evaluated variables (state) for the current context.
     */
    public void setVarsMap(VarsMap varsMap) {
        this.varsMap = varsMap;
    }

    @Override
    public CollectionMap getCollectionMap() {
        return collectionMap;
    }

    @Override
    public RollupRules getRollupRules() {
        if (rollupRules == null)
            rollupRules = new RollupRules();
        return rollupRules;
    }

    @Override
    public Eval getEval() {
        return eval;
    }

    @Override
    public WebDriverElementFinder getElementFinder() {
        return elementFinder;
    }

    @Override
    public PageInformation getLatestPageInformation() {
        return latestPageInformation;
    }

    @Override
    public void setLatestPageInformation(PageInformation pageInformation) {
        this.latestPageInformation = pageInformation;
    }

    @Override
    public CookieFilter getCookieFilter() {
        return cookieFilter;
    }

    @Override
    public void setCookieFilter(CookieFilter cookieFilter) {
        this.cookieFilter = cookieFilter;
    }

    /**
     * Execute test-suite / test-case.
     *
     * @param selenese test-suite or test-case.
     * @return result.
     */
    public Result execute(Selenese selenese) {
        try {
            return selenese.execute(null, this);
        } catch (InvalidSeleneseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isTrue(String expr) {
        return (Boolean) eval.eval(driver, varsMap.replaceVars(expr), "Boolean");
    }

    /**
     * Run Selenese script files.
     *
     * @param filenames Selenese script filenames.
     * @return result.
     */
    public Result run(String... filenames) {
        Result totalResult = UNEXECUTED;
        List<TestSuite> testSuiteList = new ArrayList<>();
        for (String filename : filenames) {
            Selenese selenese = Parser.parse(filename, commandFactory);
            Parser.setContextForBackwardCompatibility(selenese, this);
            if (selenese.isError()) {
                log.error(selenese.toString());
                totalResult = ((ErrorSource) selenese).getResult();
                continue;
            }
            switch (selenese.getType()) {
            case TEST_SUITE:
                testSuiteList.add((TestSuite) selenese);
                break;
            case TEST_CASE:
                TestSuite testSuite = Binder.newTestSuite(filename, selenese.getName());
                testSuite.addSelenese(selenese);
                testSuiteList.add(testSuite);
                break;
            }
        }
        if (totalResult != UNEXECUTED)
            return totalResult;
        for (TestSuite testSuite : testSuiteList) {
            Result result;
            try {
                result = execute(testSuite);
            } catch (RuntimeException e) {
                log.error(e.getMessage());
                throw e;
            }
            totalResult = totalResult.update(result);
        }
        return totalResult;
    }

    /**
     * Run Selenese script from input stream.
     *
     * @param filename selenese script file. (not open. used for label or generating output filename)
     * @param is input stream of script file. (test-case or test-suite)
     * @return result.
     */
    public Result run(String filename, InputStream is) {
        TestSuite testSuite;
        Selenese selenese = Parser.parse(filename, is, commandFactory);
        Parser.setContextForBackwardCompatibility(selenese, this);
        switch (selenese.getType()) {
        case TEST_CASE:
            testSuite = Binder.newTestSuite(filename, selenese.getName());
            testSuite.addSelenese(selenese);
            break;
        case TEST_SUITE:
            testSuite = (TestSuite) selenese;
            break;
        default:
            // don't reach here.
            throw new RuntimeException("Unknown Selenese object: " + selenese);
        }
        return testSuite.execute(null, this);
    }

    /**
     * Initialize JUnitResult.
     *
     * @param dir JUnit result directory.
     */
    public void setJUnitResultDir(String dir) {
        jUnitResult.setDir(dir);
        log.info("JUnit result directory: {}", dir);
    }

    @Override
    public JUnitResult getJUnitResult() {
        return jUnitResult;
    }

    /**
     * Initialize HTMLResult.
     *
     * @param dir HTML result directory.
     */
    public void setHtmlResultDir(String dir) {
        htmlResult.setDir(dir);
        log.info("HTML result directory: {}", dir);
    }

    @Override
    public HtmlResult getHtmlResult() {
        return htmlResult;
    }

    /**
     * Finish test.
     *
     * generate index.html for HTML result.
     */
    public void finish() {
        jUnitResult.generateFailsafeSummary();
        htmlResult.generateIndex();
    }

    @Override
    public void highlight(String locator, HighlightStyle highlightStyle) {
        List<Locator> selectedFrameLocators = elementFinder.getCurrentFrameLocators();
        Map<String, String> prevStyles = highlightStyle.doHighlight(driver, elementFinder, locator, selectedFrameLocators);
        if (prevStyles == null)
            return;
        HighlightStyleBackup backup = new HighlightStyleBackup(prevStyles, locator, selectedFrameLocators);
        styleBackups.push(backup);
    }

    @Override
    public void unhighlight() {
        while (!styleBackups.isEmpty()) {
            HighlightStyleBackup backup = styleBackups.pop();
            backup.restore(driver, elementFinder);
        }
    }
}
