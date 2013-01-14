package jp.vmi.selenium.selenese.inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.junit.result.JUnitResult.*;

/**
 * Interceptor for logging each command execution.
 */
public class CommandLogInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CommandLogInterceptor.class);

    private static final Comparator<Cookie> cookieComparator = new Comparator<Cookie>() {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            return c1.getName().compareTo(c2.getName());
        }
    };

    private static final FastDateFormat expiryFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private List<String> prevMessages = new ArrayList<String>();

    private void cookieToMessage(List<String> messages, Set<Cookie> cookies) {
        List<Cookie> cookieList = new ArrayList<Cookie>(cookies);
        Collections.sort(cookieList, cookieComparator);
        for (Cookie cookie : cookieList) {
            Date expiry = cookie.getExpiry();
            String expiryString = expiry != null ? expiryFormat.format(expiry) : "*";
            messages.add(String.format("- Cookie: %s=[%s] (domain=%s, path=%s, expire=%s)", cookie.getName(), cookie.getValue(),
                cookie.getDomain(), cookie.getPath(), expiryString));
        }
    }

    private void log(String cmdStr, Result result, TestCase testCase) {
        List<String> messages = getPageInformation(testCase);
        if (ListUtils.isEqualList(messages, prevMessages)) {
            if (result.isFailed()) {
                String resStr = cmdStr + " => " + result;
                log.error(resStr);
                sysErrLog(testCase, ERROR, resStr);
            } else {
                String resStr = "- " + result;
                log.info(resStr);
                sysOutLog(testCase, INFO, resStr);
            }
        } else {
            Iterator<String> iter = messages.iterator();
            String message = iter.next();
            if (result.isFailed()) {
                String resStr = cmdStr + " => " + result + " " + message;
                log.error(resStr);
                sysErrLog(testCase, ERROR, resStr);
                while (iter.hasNext()) {
                    message = iter.next();
                    log.error(message);
                    sysErrLog(testCase, ERROR, message);
                }
            } else {
                String resStr = "- " + result + " " + message;
                log.info(resStr);
                sysOutLog(testCase, INFO, resStr);
                while (iter.hasNext()) {
                    message = iter.next();
                    log.info(message);
                    sysOutLog(testCase, INFO, message);
                }
            }
            prevMessages = messages;
        }
    }

    private List<String> getPageInformation(TestCase testCase) {
        List<String> messages = new ArrayList<String>();
        WebDriver driver = testCase.getRunner().getDriver();
        try {
            String url = driver.getCurrentUrl();
            String title = driver.getTitle();
            Set<Cookie> cookies = driver.manage().getCookies();
            messages.add(String.format("URL: [%s] / Title: [%s]", url, title));
            cookieToMessage(messages, cookies);
        } catch (NoSuchWindowException e) {
            messages.add("No focused window.");
        } catch (UnhandledAlertException e) {
            String msg = e.getMessage().replaceFirst("(?s)\r?\nBuild info:.*", "");
            messages.add(String.format("No page information: [%s]", msg));
        } catch (Exception e) {
            String msg = e.getMessage().replaceFirst("(?s)\r?\nBuild info:.*", "");
            messages.add(String.format("Failed to get page information: [%s]", msg));
        }
        return messages;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestCase testCase = null;
        try {
            testCase = (TestCase) invocation.getThis();
        } catch (ClassCastException e) {
            String msg = "receiver \"" + invocation.getThis() + "\" is not TestCase: " + e;
            log.error(msg);
            sysErrLog(null, ERROR, msg);
            throw new RuntimeException(e);
        }
        Command command = (Command) invocation.getArguments()[0];
        String cmdStr = command.toString();
        log.info(cmdStr);
        sysOutLog(testCase, INFO, cmdStr);
        try {
            Result result = (Result) invocation.proceed();
            if (command.hasResult())
                log(cmdStr, result, testCase);
            return result;
        } catch (Exception e) {
            String msg = cmdStr + " => " + e.getMessage();
            log.error(msg);
            sysErrLog(testCase, ERROR, msg);
            if (testCase != null)
                setError(testCase, e.getMessage(), e.toString());
            throw e;
        }
    }
}
