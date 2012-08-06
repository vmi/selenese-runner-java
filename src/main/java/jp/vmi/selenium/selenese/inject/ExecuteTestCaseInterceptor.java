package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.utils.LoggerUtils;

public class ExecuteTestCaseInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExecuteTestCaseInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Selenese selenese;
        try {
            selenese = (Selenese) invocation.getThis();
        } catch (Exception e) {
            log.error("receiver is not Selenese", e);
            throw new RuntimeException(e);
        }
        long stime = System.nanoTime();
        log.info("Start - {}", selenese);
        try {
            return invocation.proceed();
        } finally {
            log.info("End({}) - {}", LoggerUtils.durationToString(stime, System.nanoTime()), selenese);
        }
    }
}
