package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestSuite;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

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
            log.error("receiver is not ITestSuite.", e);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        if (!testSuite.isError())
            log.info("Start: {}", testSuite);
        JUnitResult.startTestSuite(testSuite);
        try {
            return invocation.proceed();
        } catch (Throwable t) {
            log.error(t.getMessage());
            throw t;
        } finally {
            JUnitResult.endTestSuite(testSuite);
            if (!testSuite.isError())
                log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), testSuite);
        }
    }
}
