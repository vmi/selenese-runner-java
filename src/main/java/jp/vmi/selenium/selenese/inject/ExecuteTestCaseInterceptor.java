package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.junit.result.ITestSuite;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

/**
 * Interceptor for logging and recoding test-case result.
 */
public class ExecuteTestCaseInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestCaseInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ITestSuite testSuite;
        ITestCase testCase;
        try {
            testCase = (ITestCase) invocation.getThis();
            testSuite = (ITestSuite) invocation.getArguments()[0];
        } catch (Exception e) {
            log.error("receiver is not ITestCase, or 1st argument is not ITestSuite.", e);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        log.info("Start: {}", testCase);
        JUnitResult.startTestCase(testSuite, testCase);
        Result result = null;
        try {
            result = (Result) invocation.proceed();
            if (result.isSuccess())
                JUnitResult.setSuccess(testCase);
            else
                JUnitResult.setFailure(testCase, result.getMessage(), null);
            return result;
        } catch (Throwable t) {
            JUnitResult.setError(testCase, t.getMessage(), t.toString());
            throw t;
        } finally {
            if (result != null) {
                JUnitResult.addSystemOut(testCase, StringUtils.join(result.getNormalLogs(), System.getProperty("line.separator")));
                JUnitResult.addSystemErr(testCase, StringUtils.join(result.getErrorLogs(), System.getProperty("line.separator")));
            }
            JUnitResult.endTestCase(testCase);
            log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), testCase);
        }
    }
}
