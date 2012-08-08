package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.junit.JUnitResult;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

public class ExecuteTestSuiteInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestSuiteInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestSuite testSuite;
        try {
            testSuite = (TestSuite) invocation.getThis();
        } catch (Exception e) {
            log.error("receiver is not Selenese", e);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        log.info("Start: {}", testSuite);
        JUnitResult.startTestSuite(testSuite);
        try {
            return invocation.proceed();
        } finally {
            JUnitResult.endTestSuite();
            log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), testSuite);
        }
    }
}
