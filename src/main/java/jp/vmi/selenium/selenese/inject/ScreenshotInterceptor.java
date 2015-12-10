package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInvocation;
import org.openqa.selenium.WebDriverException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.ScreenshotHandler;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Interceptor for screenshot.
 */
public class ScreenshotInterceptor extends AbstractDoCommandInterceptor {

    @Override
    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
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
