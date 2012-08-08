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
import jp.vmi.selenium.selenese.junit.JUnitResult;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Provide Java API to run Selenese script.
 */
public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private WebDriver driver;
    private File screenshotDir = null;
    private boolean isScreenshotAll = false;
    private String baseURL = "";

    private void takeScreenshot(int index) {
        FastDateFormat format = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");
        if (!(driver instanceof TakesScreenshot)) {
            log.warn("webdriver is not support taking screenshot.");
            return;
        }
        TakesScreenshot taker = (TakesScreenshot) driver;
        File tmp = taker.getScreenshotAs(OutputType.FILE);
        String dateTime = format.format(Calendar.getInstance().getTime());
        File target = new File(screenshotDir, "capture_" + dateTime + "_" + index + ".png");
        if (!tmp.renameTo(target.getAbsoluteFile()))
            log.error("fail to rename file to :" + target.getAbsolutePath());
        log.info(" - capture screenshot:{}", target.getAbsolutePath());
    }

    /**
     * Take screenshot at all commands if isScreenshotAll flag if true.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @param index command index.
     */
    public void takeScreenshotAll(int index) {
        if (isScreenshotAll)
            takeScreenshot(index);
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
     * Get directory for storing screenshots.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @return directory.
     */
    public File getScreenshotDir() {
        return screenshotDir;
    }

    /**
     * Set directory for storing screenshots.
     *
     * @param screenshotDir directory.
     */
    public void setScreenshotDir(File screenshotDir) {
        this.screenshotDir = screenshotDir;
    }

    /**
     * Is taking screenshot at all commands.
     * <p>
     * <b>Internal use only.</b>
     * </p>
     * @return isScreenshotAll flag.
     */
    public boolean isScreenshotAll() {
        return isScreenshotAll;
    }

    /**
     * Set flag of taking screenshot at all commands.
     *
     * @param isScreenshotAll flag.
     */
    public void setScreenshotAll(boolean isScreenshotAll) {
        this.isScreenshotAll = isScreenshotAll;
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
     * Set directory for storing XML result files.
     *
     * @param resultDir directory.
     */
    public void setResultDir(String resultDir) {
        JUnitResult.setResultDir(resultDir);
    }

    /**
     * Run Selenese script file.
     *
     * @param file Selenese script file. (test-case or test-suite)
     * @return result.
     */
    public Result run(File file) {
        try {
            Selenese selenese = Parser.parse(file, this);
            return selenese.execute(this);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw e;
        } catch (InvalidSeleneseException e) {
            log.error(e.getMessage());
            return new Failure(e.getMessage());
        }
    }

    /**
     * Run Selenese script files.
     *
     * @param filenames Selenese script filenames.
     * @return result.
     */
    public Result run(String... filenames) {
        TestSuite testSuite = Binder.newTestSuite(null, "default");
        for (String filename : filenames)
            testSuite.addTestCase(filename);
        return testSuite.execute(this);
    }
}
