package jp.vmi.selenium.selenese.inject;

import java.io.File;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.command.Command.Result;

public class RunFilesInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RunFilesInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // arguments
        Object[] args = invocation.getArguments();
        List<File> files;
        try {
            files = (List<File>) args[0];
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
