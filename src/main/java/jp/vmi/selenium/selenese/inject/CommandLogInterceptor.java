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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;

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

    private void log(String cmdStr, Result result, TestCase testCase, Runner runner) {
        LogRecorder clr = testCase.getLogRecorder();
        List<String> messages = getPageInformation(testCase, runner);
        if (ListUtils.isEqualList(messages, prevMessages)) {
            if (result.isFailed()) {
                String resStr = cmdStr + " => " + result;
                log.error(resStr);
                clr.error(resStr);
            } else {
                String resStr = "- " + result;
                log.info(resStr);
                clr.info(resStr);
            }
        } else {
            Iterator<String> iter = messages.iterator();
            String message = iter.next();
            if (result.isFailed()) {
                String resStr = cmdStr + " => " + result + " " + message;
                log.error(resStr);
                clr.error(resStr);
                while (iter.hasNext()) {
                    message = iter.next();
                    log.error(message);
                    clr.error(message);
                }
            } else {
                String resStr = "- " + result + " " + message;
                log.info(resStr);
                clr.info(resStr);
                while (iter.hasNext()) {
                    message = iter.next();
                    log.info(message);
                    clr.info(message);
                }
            }
            prevMessages = messages;
        }
    }

    private String getMessage(Exception e) {
        String msg = e.getMessage();
        if (msg != null) {
            return msg.replaceFirst("(?s)\r?\nBuild info:.*", "");
        } else {
            List<String> msgs = new ArrayList<String>();
            msgs.add(e.toString());
            String pkgName = getClass().getPackage().getName() + ".";
            for (StackTraceElement ste : e.getStackTrace()) {
                if (ste.getClassName().startsWith(pkgName))
                    break;
                msgs.add(ste.toString().trim());
            }
            return StringUtils.join(msgs, " / at ");
        }
    }

    private List<String> getPageInformation(TestCase testCase, Runner runner) {
        List<String> messages = new ArrayList<String>();
        WebDriver driver = runner.getWrappedDriver();
        try {
            String handle = driver.getWindowHandle();
            if (StringUtils.isEmpty(handle))
                throw new NotFoundException();
            driver.switchTo().window(handle);
            String url = driver.getCurrentUrl();
            String title = driver.getTitle();
            Set<Cookie> cookies = driver.manage().getCookies();
            messages.add(String.format("URL: [%s] / Title: [%s]", url, title));
            cookieToMessage(messages, cookies);
        } catch (NotFoundException e) {
            messages.add("No focused window/frame.");
        } catch (UnhandledAlertException e) {
            messages.add(String.format("No page information: [%s]", getMessage(e)));
        } catch (Exception e) {
            messages.add(String.format("Failed to get page information: [%s]", getMessage(e)));
        }
        return messages;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestCase testCase = (TestCase) invocation.getThis();
        Object[] args = invocation.getArguments();
        Command command = (Command) args[0];
        Runner runner = (Runner) args[1];
        String cmdStr = command.toString();
        LogRecorder clr = testCase.getLogRecorder();
        log.info(cmdStr);
        clr.info(cmdStr);
        try {
            Result result = (Result) invocation.proceed();
            if (command.hasResult())
                log(cmdStr, result, testCase, runner);
            return result;
        } catch (Exception e) {
            String msg = cmdStr + " => " + e.getMessage();
            log.error(msg);
            clr.error(msg);
            if (testCase != null)
                runner.getJUnitResult().setError(testCase, e.getMessage(), e.toString());
            throw e;
        }
    }
}
