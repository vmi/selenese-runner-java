package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Provide Java API to run Selenese script.
 */
public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private static final FastDateFormat FILE_DATE_TIME = FastDateFormat.getInstance("yyyyMMdd_HHmmssSSS");

    private WebDriver driver;
    private String screenshotDir = null;
    private String screenshotAllDir = null;
    private String screenshotOnFailDir = null;
    private String baseURL = "";

    private final int countForDefault = 0;

    private void takeScreenshot(File file) {
        File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        if (!tmp.renameTo(file))
            throw new RuntimeException("failed to rename captured screenshot image: " + file);
        log.info(" - captured screenshot: {}", file);
        log.info("[[ATTACHMENT|{}]]", file.getAbsolutePath());
    }

    /**
     * Take screenshot to filename. (override directory if --screenshot-dir option specified)
     * <p>
     * <p>Internal use only.</p>
     * </p>
     * @param filename filename.
     * @exception UnsupportedOperationException WebDriver does not supoort capturing screenshot.
     */
    public void takeScreenshot(String filename) throws UnsupportedOperationException {
        if (!(driver instanceof TakesScreenshot))
            throw new UnsupportedOperationException("webdriver does not support capturing screenshot.");
        File file = new File(filename).getAbsoluteFile();
        if (screenshotDir != null)
            file = new File(screenshotDir, file.getName()).getAbsoluteFile();
        takeScreenshot(file);
    }

    /**
     * Take screenshot at all commands if --screenshot-all option specified.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @param prefix prefix name.
     * @param index command index.
     */
    public void takeScreenshotAll(String prefix, int index) {
        if (screenshotAllDir == null || !(driver instanceof TakesScreenshot))
            return;
        String filename = String.format("%s_%s_%d.png", prefix, FILE_DATE_TIME.format(Calendar.getInstance()), index);
        takeScreenshot(new File(screenshotAllDir, filename));
    }

    /**
     * Take screenshot on fail commands if --screenshot-on-fail option specified.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @param prefix prefix name.
     * @param index command index.
     */
    public void takeScreenshotOnFail(String prefix, int index) {
        if (screenshotOnFailDir == null || !(driver instanceof TakesScreenshot))
            return;
        String filename = String.format("%s_%s_%d_fail.png", prefix, FILE_DATE_TIME.format(Calendar.getInstance()), index);
        takeScreenshot(new File(screenshotOnFailDir, filename));
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

    public String getScreenshotOnFailDir() {
        return screenshotOnFailDir;
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
     * Run Selenese script files.
     *
     * @param filenames Selenese script filenames.
     * @return result.
     */
    public Result run(String... filenames) {
        TestSuite testSuite = Binder.newTestSuite(null, String.format("default-%02d", countForDefault), this);
        for (String filename : filenames)
            testSuite.addTestCase(filename);
        return testSuite.execute(null);
    }
}
