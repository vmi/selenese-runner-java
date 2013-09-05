package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openqa.selenium.NoSuchWindowException;

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
        TestCase testCase = (TestCase) invocation.getThis();
        Object[] args = invocation.getArguments();
        Command command = (Command) args[0];
        Runner runner = (Runner) args[1];
        Result result = (Result) invocation.proceed();
        if (command.canUpdate()) {
            String baseName = testCase.getBaseName();
            try {
                runner.takeScreenshotAll(baseName, command.getIndex(), testCase);
            } catch (NoSuchWindowException e) {
                // ignore if failed to capturing.
            }
            if (!result.isSuccess())
                runner.takeScreenshotOnFail(baseName, command.getIndex(), testCase);
        }
        return result;
    }
}
