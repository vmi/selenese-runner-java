package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.junit.result.ITestSuite;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.StopWatch;

import static jp.vmi.junit.result.JUnitResult.*;

/**
 * Interceptor for logging and recoding test-case result.
 */
public class ExecuteTestCaseInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestCaseInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ITestCase testCase = (ITestCase) invocation.getThis();
        ITestSuite testSuite = (ITestSuite) invocation.getArguments()[0];
        StopWatch sw = testCase.getStopWatch();
        LogRecorder clr = testCase.getLogRecorder();
        sw.start();
        startTestCase(testSuite, testCase);
        if (!testCase.isError()) {
            log.info("Start: {}", testCase);
            clr.info("Start: " + testCase);
        }
        if (testCase instanceof TestCase) {
            String baseURL = ((TestCase) testCase).getBaseURL();
            log.info("baseURL: {}", baseURL);
            clr.info("baseURL: " + baseURL);
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
            clr.error(msg);
            setError(testCase, msg, t.toString());
            throw t;
        } finally {
            sw.end();
            if (!testCase.isError()) {
                String msg = "End(" + sw.getDurationString() + "): " + testCase;
                log.info(msg);
                clr.info(msg);
            }
            endTestCase(testCase);
        }
    }
}
