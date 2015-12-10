package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.html.result.HtmlResult;
import jp.vmi.html.result.HtmlResultHolder;
import jp.vmi.junit.result.JUnitResult;
import jp.vmi.junit.result.JUnitResultHolder;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.StopWatch;
import jp.vmi.selenium.selenese.utils.SystemInformation;

/**
 * Interceptor for logging and recoding test-suite result.
 */
public class ExecuteTestSuiteInterceptor extends AbstractExecuteTestSuiteInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestSuiteInterceptor.class);

    private void initTestSuiteResult(JUnitResult jUnitResult, TestSuite testSuite) {
        jUnitResult.startTestSuite(testSuite);
        SystemInformation sysInfo = SystemInformation.getInstance();
        jUnitResult.addProperty(testSuite, "seleneseRunner.version", sysInfo.getSeleneseRunnerVersion());
        jUnitResult.addProperty(testSuite, "selenium.version", sysInfo.getSeleniumVersion());
        jUnitResult.addProperty(testSuite, "selenium.webDriver.name", testSuite.getWebDriverName());
    }

    @Override
    protected Result invoke(MethodInvocation invocation, TestSuite testSuite, Selenese parent, Context context) throws Throwable {
        testSuite.setWebDriverName(context.getWrappedDriver().getClass().getSimpleName());
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
        initTestSuiteResult(jUnitResult, testSuite);
        try {
            return (Result) invocation.proceed();
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
