package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.junit.result.JUnitResult.*;

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
            String msg = "receiver is not ITestCase, or 1st argument is not ITestSuite: " + e;
            log.error(msg);
            sysErrLog(null, ERROR, msg);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        startTestCase(testSuite, testCase);
        if (!testCase.isError()) {
            log.info("Start: {}", testCase);
            sysOutLog(testCase, INFO, "Start: " + testCase);
        }
        if (testCase instanceof TestCase) {
            String baseURL = ((TestCase) testCase).getBaseURL();
            log.info("baseURL: {}", baseURL);
            sysOutLog(testCase, INFO, "baseURL: " + baseURL);
        }
        try {
            Result result = (Result) invocation.proceed();
            if (result.isSuccess())
                setSuccess(testCase);
            else
                setFailure(testCase, result.getMessage(), null);
            return result;
        } catch (Throwable t) {
            String msg = t.getMessage();
            log.error(msg);
            sysErrLog(testCase, ERROR, msg);
            setError(testCase, msg, t.toString());
            throw t;
        } finally {
            if (!testCase.isError()) {
                String msg = "End(" + LoggerUtils.durationToString(stime, System.nanoTime()) + "): " + testCase;
                log.info(msg);
                sysOutLog(testCase, INFO, msg);
            }
            endTestCase(testCase);
        }
    }
}
