package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Interceptor for screenshot.
 */
public class ScreenshotInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Result result = (Result) invocation.proceed();
        Command command = (Command) invocation.getArguments()[0];
        if (command.canUpdate()) {
            TestCase testCase = (TestCase) invocation.getThis();
            Runner runner = testCase.getRunner();
            String baseName = testCase.getBaseName();
            runner.takeScreenshotAll(baseName, command.getIndex(), testCase);
            if (!result.isSuccess())
                runner.takeScreenshotOnFail(baseName, command.getIndex(), testCase);
        }
        return result;
    }
}
