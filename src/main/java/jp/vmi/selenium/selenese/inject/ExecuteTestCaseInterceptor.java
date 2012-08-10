package jp.vmi.selenium.selenese.inject;

import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.junit.JUnitResult;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

/**
 * Interceptor for logging and recoding test-case result.
 */
public class ExecuteTestCaseInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestCaseInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestCase testCase;
        try {
            testCase = (TestCase) invocation.getThis();
        } catch (Exception e) {
            log.error("receiver is not Selenese", e);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        log.info("Start: {}", testCase);
        JUnitResult.startTestCase(testCase);
        ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>();
        appender.start();
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(appender);
        try {
            Result result = (Result) invocation.proceed();
            if (result.isFailed())
                JUnitResult.addFailure(result.getMessage());
            return result;
        } finally {
            List<String> normalLogs = new LinkedList<String>();
            List<String> errorLogs = new LinkedList<String>();
            for (ILoggingEvent e : appender.list) {
                normalLogs.add(e.getFormattedMessage());
                if (e.getLevel().isGreaterOrEqual(Level.ERROR))
                    errorLogs.add(e.getFormattedMessage());
            }
            JUnitResult.setSystemOut(StringUtils.join(normalLogs, System.getProperty("line.separator")));
            JUnitResult.setSystemErr(StringUtils.join(errorLogs, System.getProperty("line.separator")));
            root.detachAppender(appender);
            appender.stop();
            JUnitResult.endTestCase();
            log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), testCase);
        }
    }
}
