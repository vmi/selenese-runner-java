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

import jp.vmi.selenium.selenese.command.Command.Failure;
import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.junit.JUnitResult;

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

    public void takeScreenshotAll(int index) {
        if (isScreenshotAll)
            takeScreenshot(index);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public File getScreenshotDir() {
        return screenshotDir;
    }

    public void setScreenshotDir(File screenshotDir) {
        this.screenshotDir = screenshotDir;
    }

    public boolean isScreenshotAll() {
        return isScreenshotAll;
    }

    public void setScreenshotAll(boolean isScreenshotAll) {
        this.isScreenshotAll = isScreenshotAll;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public String getBaseURL(String baseURL) {
        if (StringUtils.isBlank(this.baseURL))
            return baseURL;
        else
            return this.baseURL;
    }

    public void setResultDir(String resultDir) {
        JUnitResult.setResultDir(resultDir);
    }

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

    public Result run(String[] filenames) {
        TestSuite testSuite = Binder.newTestSuite(null, "default");
        for (String filename : filenames)
            testSuite.addTestCase(filename);
        return testSuite.execute(this);
    }
}
