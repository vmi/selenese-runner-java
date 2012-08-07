package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.selenese.junit.JUnitResult;
import jp.vmi.selenium.utils.LoggerUtils;

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
        try {
            Result result = (Result) invocation.proceed();
            if (result.isFailed())
                JUnitResult.addFailure(result.getMessage());
            return result;
        } finally {
            JUnitResult.endTestCase();
            log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), testCase);
        }
    }
}
