package jp.vmi.selenium.selenese;

import java.io.File;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.utils.LoggerUtils;

public class RunFileInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RunFileInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // arguments
        Object[] args = invocation.getArguments();
        File file;
        try {
            file = (File) args[0];
        } catch (Exception e) {
            log.error("method arguments error", e);
            throw new RuntimeException(e);
        }

        long stime = System.nanoTime();
        String name = file.getName();
        log.info("Start: {}", name);
        Result r;
        try {
            r = (Result) invocation.proceed();
        } finally {
            log.info("End({}): {}", LoggerUtils.durationToString(stime, System.nanoTime()), name);
        }
        return r;
    }

}
