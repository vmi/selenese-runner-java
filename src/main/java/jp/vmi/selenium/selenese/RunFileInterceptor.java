package jp.vmi.selenium.selenese;

import java.io.File;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.Command.Result;

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

        //TODO before run file
        Result r;
        try {
            r = (Result) invocation.proceed();
        } finally {
            //TODO after run file
        }
        return r;
    }

}
