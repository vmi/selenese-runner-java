package jp.vmi.selenium.selenese;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.html.result.HtmlResult;
import jp.vmi.html.result.HtmlResultHolder;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;

import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * Provide Java API to run Selenese script.
 */
public class Runner implements HtmlResultHolder {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private static final FastDateFormat FILE_DATE_TIME = FastDateFormat.getInstance("yyyyMMdd_HHmmssSSS");

    private WebDriver driver;
    private String screenshotDir = null;
    private String screenshotAllDir = null;
    private String screenshotOnFailDir = null;
    private String baseURL = "";
    private boolean ignoreScreenshotCommand = false;
    private boolean isHighlight = false;
    private int timeout = 30 * 1000; /* ms */
    private Map<String, Object> varsMap = new HashMap<String, Object>();

    private int countForDefault = 0;

    private HtmlResult htmlResult = null;

    private void takeScreenshot(File file, TestCase testcase) {
        File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.moveFile(tmp, file);
        } catch (IOException e) {
            throw new RuntimeException("failed to rename captured screenshot image: " + file, e);
        }
        log.info("- captured screenshot: {}", file);
        testcase.getLogRecorder().info("[[ATTACHMENT|" + file.getAbsolutePath() + "]]");
    }

    /**
     * Take screenshot to filename. (override directory if --screenshot-dir option specified)
     * <p>
     * <p>Internal use only.</p>
     * </p>
     * @param filename filename.
     * @param testcase test-case instance.
     * @exception UnsupportedOperationException WebDriver does not supoort capturing screenshot.
     */
    public void takeScreenshot(String filename, TestCase testcase) throws UnsupportedOperationException {
        if (!(driver instanceof TakesScreenshot))
            throw new UnsupportedOperationException("webdriver does not support capturing screenshot.");
        if (File.separatorChar != '\\' && filename.contains("\\"))
            filename = filename.replace('\\', File.separatorChar);
        File file = new File(filename).getAbsoluteFile();
        if (screenshotDir != null)
            file = new File(screenshotDir, file.getName()).getAbsoluteFile();
        takeScreenshot(file, testcase);
    }

    /**
     * Take screenshot at all commands if --screenshot-all option specified.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @param prefix prefix name.
     * @param index command index.
     * @param testcase test-case instance.
     */
    public void takeScreenshotAll(String prefix, int index, TestCase testcase) {
        if (screenshotAllDir == null || !(driver instanceof TakesScreenshot))
            return;
        String filename = String.format("%s_%s_%d.png", prefix, FILE_DATE_TIME.format(Calendar.getInstance()), index);
        takeScreenshot(new File(screenshotAllDir, filename), testcase);
    }

    /**
     * Take screenshot on fail commands if --screenshot-on-fail option specified.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @param prefix prefix name.
     * @param index command index.
     * @param testcase test-case instance.
     */
    public void takeScreenshotOnFail(String prefix, int index, TestCase testcase) {
        if (screenshotOnFailDir == null || !(driver instanceof TakesScreenshot))
            return;
        String filename = String.format("%s_%s_%d_fail.png", prefix, FILE_DATE_TIME.format(Calendar.getInstance()), index);
        takeScreenshot(new File(screenshotOnFailDir, filename), testcase);
    }

    /**
     * Get WebDriver.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @return WebDriver.
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Set WebDriver.
     *
     * @param driver WebDriver.
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Set directory for storing screenshots.
     *
     * @param screenshotDir directory.
     * @exception IllegalArgumentException throws if screenshotDir is not directory.
     */
    public void setScreenshotDir(String screenshotDir) throws IllegalArgumentException {
        if (screenshotDir != null && !new File(screenshotDir).isDirectory())
            throw new IllegalArgumentException(screenshotDir + " is not directory.");
        this.screenshotDir = screenshotDir;
    }

    /**
     * Set directory for storing screenshots at all commands.
     *
     * @param screenshotAllDir directory.
     * @exception IllegalArgumentException throws if screenshotAllDir is not directory.
     */
    public void setScreenshotAllDir(String screenshotAllDir) throws IllegalArgumentException {
        if (screenshotAllDir != null && !new File(screenshotAllDir).isDirectory())
            throw new IllegalArgumentException(screenshotAllDir + " is not directory.");
        this.screenshotAllDir = screenshotAllDir;
    }

    /**
     * Set directory for storing screenshot on fail.
     *
     * @param screenshotOnFailDir directory.
     */
    public void setScreenshotOnFailDir(String screenshotOnFailDir) {
        if (screenshotOnFailDir != null && !new File(screenshotOnFailDir).isDirectory())
            throw new IllegalArgumentException(screenshotOnFailDir + " is not directory.");
        this.screenshotOnFailDir = screenshotOnFailDir;
    }

    /**
     * Get URL for overriding selenium.base in Selenese script.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @return URL.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Set URL for overriding selenium.base in Selenese script.
     *
     * @param baseURL URL.
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Set ignore screenshot command flag.
     *
     * @param ignoreScreenshotCommand set true if you want to ignore "captureEntirePageScreenshot"
     */
    public void setIgnoreScreenshotCommand(boolean ignoreScreenshotCommand) {
        this.ignoreScreenshotCommand = ignoreScreenshotCommand;
    }

    /**
     * Get ignore screenshot command flag.
     *
     * @return flag to ignore "captureEntirePageScreenshot"
     */
    public boolean isIgnoreScreenshotCommand() {
        return ignoreScreenshotCommand;
    }

    /**
     * Get locator highlighting.
     *
     * @return true if use locator highlighting.
     */
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
    }

    /**
     * Get <b>effective</b> base URL for running Selenese script.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @param baseURL URL specified in file.
     * @return effective URL.
     */
    public String getEffectiveBaseURL(String baseURL) {
        if (StringUtils.isBlank(this.baseURL))
            return baseURL;
        else
            return this.baseURL;
    }

    /**
     * Get timeout for waiting. (ms)
     *
     * @return timeout for waiting.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Set timeout for waiting. (ms)
     *
     * @param timeout for waiting.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Get timeout for waiting. (ms)
     *
     * @return the evaluated variables (state) for the current context.
     */
    public Map<String, Object> getVarsMap() {
        return this.varsMap;
    }

    /**
     * Set variables map used for this session
     *
     * @param varsMap the evaluated variables (state) for the current context.
     */
    public void setVarsMap(Map<String, Object> varsMap) {
        this.varsMap = varsMap;
    }

    /**
     * Run Selenese script files.
     *
     * @param filenames Selenese script filenames.
     * @return result.
     */
    public Result run(String... filenames) {
        Result totalResult = UNEXECUTED;
        TestSuite defaultTestSuite = null;
        List<TestSuite> testSuiteList = new ArrayList<TestSuite>();
        for (String filename : filenames) {
            Selenese selenese = Parser.parse(filename, this);
            if (selenese instanceof TestSuite) {
                testSuiteList.add((TestSuite) selenese);
            } else if (selenese instanceof TestCase) {
                if (defaultTestSuite == null) {
                    defaultTestSuite = Binder.newTestSuite(null, String.format("default-%02d", countForDefault++), this);
                    testSuiteList.add(defaultTestSuite);
                }
                defaultTestSuite.addTestCase((TestCase) selenese);
            } else if (selenese instanceof ErrorSource) {
                log.error(selenese.toString());
                totalResult = new Error("Invalid parameter");
            } else {
                throw new RuntimeException("Unknown Selenese object: " + selenese);
            }
        }
        if (totalResult != UNEXECUTED)
            return totalResult;
        for (TestSuite testSuite : testSuiteList) {
            Result result;
            try {
                result = testSuite.execute(null, this);
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
        Selenese selenese = Parser.parse(filename, is, this);
        if (selenese instanceof TestCase) {
            testSuite = Binder.newTestSuite(null, String.format("default-%02d", countForDefault++), this);
            testSuite.addTestCase((TestCase) selenese);
        } else {
            testSuite = (TestSuite) selenese;
        }
        return testSuite.execute(null, this);
    }

    /**
     * Set directory for JUnit result xml files.
     *
     * @param dir result directory.
     */
    public void setResultDir(String dir) {
        JUnitResult.setXmlResultDir(dir);
    }

    /**
     * Set directory for HTML result files.
     * 
     * @param dir result directory.
     */
    public void setHtmlResultDir(String dir) {
        htmlResult = new HtmlResult(dir);
    }

    /**
     * Get HTML result instance.
     *
     * @return HTML result instance.
     */
    @Override
    public HtmlResult getHtmlResult() {
        return htmlResult;
    }

    /**
     * set PrintStream for logging.
     * @param out PrintStream for logging.
     */
    public static void setPrintStream(PrintStream out) {
        LogRecorder.setPrintStream(out);
    }
}
