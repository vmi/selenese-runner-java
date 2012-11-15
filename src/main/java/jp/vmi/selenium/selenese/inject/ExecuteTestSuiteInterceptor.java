package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.junit.result.JUnitResult.*;

/**
 * Interceptor for logging and recoding test-suite result.
 */
public class ExecuteTestSuiteInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestSuiteInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ITestSuite testSuite;
        try {
            testSuite = (ITestSuite) invocation.getThis();
        } catch (Exception e) {
            String msg = "receiver is not ITestSuite: " + e;
            log.error(msg);
            logError(null, msg);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        if (!testSuite.isError()) {
            log.info("Start: {}", testSuite);
            logInfo(null, "Start:", testSuite.toString());
        }
        JUnitResult.startTestSuite(testSuite);
        try {
            return invocation.proceed();
        } catch (Throwable t) {
            String msg = t.getMessage();
            log.error(msg);
            logError(null, msg);
            throw t;
        } finally {
            JUnitResult.endTestSuite(testSuite);
            if (!testSuite.isError()) {
                String msg = "End(" + LoggerUtils.durationToString(stime, System.nanoTime()) + "): " + testSuite;
                log.info(msg);
                logInfo(null, msg);
            }
        }
    }
}
