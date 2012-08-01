package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.Command.Failure;
import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.utils.LoggerUtils;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static jp.vmi.selenium.selenese.command.Command.*;

public class Runner {

    private static final Logger log = LoggerFactory.getLogger(Runner.class);

    private static final Comparator<Cookie> cookieComparator = new Comparator<Cookie>() {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            return c1.getName().compareTo(c2.getName());
        }
    };

    private static final FastDateFormat expiryFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final WebDriver driver;
    private File screenshotDir = null;
    private boolean isScreenshotAll = false;
    private String baseURI = "";

    private List<String> prevMessages = new ArrayList<String>();

    private void cookieToMessage(List<String> messages) {
        List<Cookie> cookieList = new ArrayList<Cookie>(driver.manage().getCookies());
        Collections.sort(cookieList, cookieComparator);
        for (Cookie cookie : cookieList) {
            Date expiry = cookie.getExpiry();
            String expiryString = expiry != null ? expiryFormat.format(expiry) : "*";
            messages.add(String.format("- Cookie: %s=[%s] (domain=%s, path=%s, expire=%s)", cookie.getName(), cookie.getValue(),
                cookie.getDomain(), cookie.getPath(), expiryString));
        }
    }

    private void log(Result result) {
        List<String> messages = new ArrayList<String>();
        messages.add(String.format("URL: [%s] / Title: [%s]", driver.getCurrentUrl(), driver.getTitle()));
        cookieToMessage(messages);
        if (ListUtils.isEqualList(messages, prevMessages)) {
            if (result.isFailed()) {
                log.error("- {}", result);
            } else {
                log.info("- {}", result);
            }
        } else {
            Iterator<String> iter = messages.iterator();
            if (result.isFailed()) {
                log.error("- {} {}", result, iter.next());
                while (iter.hasNext())
                    log.error(iter.next());
            } else {
                log.info("- {} {}", result, iter.next());
                while (iter.hasNext())
                    log.info(iter.next());
            }
            prevMessages = messages;
        }
    }

    private void takeScreenshot(int index) {
        FastDateFormat format = FastDateFormat.getInstance("yyyyMMddHHmmssSSS");
        if (isScreenshotAll) {
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
    }

    public Runner() {
        this(WebDriverManager.getInstance().get());
    }

    public Runner(WebDriver driver) {
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

    public String getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public Result evaluate(Context context, Command current) {
        Result totalResult = SUCCESS;
        while (current != null) {
            log.info(current.toString());
            Result result = current.doCommand(context);
            log(result);
            takeScreenshot(current.getIndex());
            totalResult = totalResult.update(result);
            if (totalResult.isInterrupted())
                break;
            current = current.next(context);
        }
        return totalResult;
    }

    public Result run(File file) {
        long stime = System.nanoTime();
        String name = file.getName();
        try {
            log.info("Start: {}", name);
            Parser parser = Parser.getParser(file);
            if (parser instanceof TestSuiteParser) {
                Result totalResult = SUCCESS;
                for (File tcFile : ((TestSuiteParser) parser).getTestCaseFiles())
                    totalResult = totalResult.update(run(tcFile));
                return totalResult;
            } else { // if parser instanceof TestCaseParser
                TestCaseParser tcParser = (TestCaseParser) parser;
                String baseURI = StringUtils.isBlank(this.baseURI) ? tcParser.getBaseURI() : this.baseURI;
                WebDriverCommandProcessor proc = new WebDriverCommandProcessor(baseURI, driver);
                Context context = new Context(proc);
                return evaluate(context, tcParser.parse(proc, context));
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            throw e;
        } catch (InvalidSeleneseException e) {
            log.error(e.getMessage());
            return new Failure(e.getMessage());
        } finally {
            log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), name);
        }
    }

    public Result run(List<File> files) {
        Result totalResult = SUCCESS;
        for (File file : files)
            totalResult = totalResult.update(run(file));
        return totalResult;
    }
}
