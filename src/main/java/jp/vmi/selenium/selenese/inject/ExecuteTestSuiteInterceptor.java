package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.html.result.HtmlResult;
import jp.vmi.html.result.HtmlResultHolder;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.junit.result.JUnitResultHolder;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 * Interceptor for logging and recoding test-suite result.
 */
public class ExecuteTestSuiteInterceptor implements MethodInterceptor {

    @SuppressWarnings("unused")
    private static final int PARENT = 0;
    private static final int CONTEXT = 1;

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestSuiteInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestSuite testSuite = (TestSuite) invocation.getThis();
        Context context = (Context) invocation.getArguments()[CONTEXT];
        JUnitResult jUnitResult = (context instanceof JUnitResultHolder) ? ((JUnitResultHolder) context).getJUnitResult() : null;
        HtmlResult htmlResult = (context instanceof HtmlResultHolder) ? ((HtmlResultHolder) context).getHtmlResult() : null;
        StopWatch sw = testSuite.getStopWatch();
        LogRecorder slr = new LogRecorder(context.getPrintStream());
        sw.start();
        if (!testSuite.isError()) {
            String msg = "Start: " + testSuite;
            log.info(msg);
            slr.info(msg);
        }
        jUnitResult.startTestSuite(testSuite);
        try {
            return invocation.proceed();
        } catch (Throwable t) {
            String msg = t.getMessage();
            log.error(msg);
            slr.error(msg);
            throw t;
        } finally {
            if (!testSuite.isError()) {
                String msg = "End(" + sw.getDurationString() + "): " + testSuite;
                log.info(msg);
                slr.info(msg);
            }
            sw.end();
            if (jUnitResult != null)
                jUnitResult.endTestSuite(testSuite);
            if (htmlResult != null)
                htmlResult.generate(testSuite);
        }
    }
}
