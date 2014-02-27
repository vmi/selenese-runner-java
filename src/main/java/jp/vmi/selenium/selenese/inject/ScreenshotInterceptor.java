package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openqa.selenium.NoSuchWindowException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.ICommand;

/**
 * Interceptor for screenshot.
 */
public class ScreenshotInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestCase testCase = (TestCase) invocation.getThis();
        Object[] args = invocation.getArguments();
        Context context = (Context) args[0];
        ICommand command = (ICommand) args[1];
        Object result = invocation.proceed();
        if (context instanceof Runner && command.mayUpdateScreen()) {
            Runner runner = (Runner) context;
            String baseName = testCase.getBaseName();
            try {
                runner.takeScreenshotAll(baseName, command.getIndex(), testCase);
            } catch (NoSuchWindowException e) {
                // ignore if failed to capturing.
            }
            if (!command.getResult().isSuccess())
                runner.takeScreenshotOnFail(baseName, command.getIndex(), testCase);
        }
        return result;
    }
}
