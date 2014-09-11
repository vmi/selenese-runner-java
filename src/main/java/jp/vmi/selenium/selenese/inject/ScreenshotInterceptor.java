package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.openqa.selenium.WebDriverException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.ScreenshotHandler;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Interceptor for screenshot.
 */
public class ScreenshotInterceptor implements MethodInterceptor {

    private static final int CONTEXT = 0;
    private static final int COMMAND = 1;

    /*
     * target signature:
     * Result doCommand(Context context, ICommand command, String... curArgs)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Context context = (Context) args[CONTEXT];
        ICommand command = (ICommand) args[COMMAND];
        Result result = (Result) invocation.proceed();
        if (context instanceof ScreenshotHandler && command.mayUpdateScreen()) {
            ScreenshotHandler handler = (ScreenshotHandler) context;
            String baseName = context.getCurrentTestCase().getBaseName();
            try {
                command.addScreenshot(handler.takeScreenshotAll(baseName, command.getIndex()), "all");
                if (!result.isSuccess())
                    command.addScreenshot(handler.takeScreenshotOnFail(baseName, command.getIndex()), "fail");
            } catch (WebDriverException e) {
                // ignore if failed to capturing.
            }
        }
        return result;
    }
}
