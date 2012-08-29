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
import org.apache.commons.lang.time.FastDateFormat;
import org.openqa.selenium.Cookie;
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

    private void log(Result result, TestCase testCase) {
        List<String> messages = new ArrayList<String>();
        WebDriver driver = testCase.getRunner().getDriver();
        messages.add(String.format("URL: [%s] / Title: [%s]", driver.getCurrentUrl(), driver.getTitle()));
        cookieToMessage(messages, driver.manage().getCookies());
        if (ListUtils.isEqualList(messages, prevMessages)) {
            if (result.isFailed()) {
                log.error("- {}", result);
                logError(testCase, "-", result.toString());
            } else {
                log.info("- {}", result);
                logInfo(testCase, "-", result.toString());
            }
        } else {
            Iterator<String> iter = messages.iterator();
            String message = iter.next();
            if (result.isFailed()) {
                log.error("- {} {}", result, message);
                logError(testCase, "-", result.toString(), message);
                while (iter.hasNext()) {
                    message = iter.next();
                    log.error(message);
                    logError(testCase, message);
                }
            } else {
                log.info("- {} {}", result, message);
                logInfo(testCase, "-", result.toString(), message);
                while (iter.hasNext()) {
                    message = iter.next();
                    log.info(message);
                    logInfo(testCase, message);
                }
            }
            prevMessages = messages;
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestCase testCase = null;
        try {
            testCase = (TestCase) invocation.getThis();
            Command command = (Command) invocation.getArguments()[0];
            log.info(command.toString());
            logInfo(testCase, command.toString());
            Result result = (Result) invocation.proceed();
            if (command.hasResult())
                log(result, testCase);
            return result;
        } catch (ClassCastException e) {
            log.error("receiver \"{}\" is not TestCase: {}", invocation.getThis(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            if (testCase != null)
                setError(testCase, e.getMessage(), e.toString());
            throw e;
        }
    }
}
