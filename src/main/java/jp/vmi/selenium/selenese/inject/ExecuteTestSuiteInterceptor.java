package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.html.result.HtmlResult;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.junit.result.JUnitResult.*;

/**
 * Interceptor for logging and recoding test-suite result.
 */
public class ExecuteTestSuiteInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestSuiteInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestSuite testSuite = (TestSuite) invocation.getThis();
        Runner runner = (Runner) invocation.getArguments()[1];
        HtmlResult htmlResult = runner.getHtmlResult();
        long stime = System.nanoTime();
        if (!testSuite.isError()) {
            log.info("Start: {}", testSuite);
            sysOutLog(null, INFO, "Start: " + testSuite);
        }
        JUnitResult.startTestSuite(testSuite);
        try {
            return invocation.proceed();
        } catch (Throwable t) {
            String msg = t.getMessage();
            log.error(msg);
            sysErrLog(null, ERROR, msg);
            throw t;
        } finally {
            JUnitResult.endTestSuite(testSuite);
            if (!testSuite.isError()) {
                String msg = "End(" + LoggerUtils.durationToString(stime, System.nanoTime()) + "): " + testSuite;
                log.info(msg);
                sysOutLog(null, INFO, msg);
            }
            if (htmlResult != null)
                htmlResult.generate(testSuite);
        }
    }
}
