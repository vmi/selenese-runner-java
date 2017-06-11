package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.result.MaxTimeExceeded;
import jp.vmi.selenium.selenese.result.Result;

import static java.lang.Thread.*;
import static jp.vmi.selenium.selenese.MaxTimeActiveTimer.*;

/**
 * Interceptor class handles interruption made by {@link jp.vmi.selenium.selenese.MaxTimeActiveTimer}.
 */
public class MaxTimeInterruptInterceptor extends AbstractDoCommandInterceptor {

    @Override
    protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable {
        Result result = null;
        if (isInterruptedByMaxTimeTimer(currentThread())) {
            interrupted(); // clear interrupted status only when the thread is interrupted by MaxTimeActiveTimer
            return new MaxTimeExceeded();
        }
        try {
            result = (Result) invocation.proceed();
        } catch (Throwable t) {
            if (isInterruptedByMaxTimeTimer(currentThread())) {
                interrupted();
                return new MaxTimeExceeded((Exception) t);
            }
            throw t;
        }
        if (isInterruptedByMaxTimeTimer(currentThread())) {
            interrupted();
            return new MaxTimeExceeded();
        }
        return result;
    }
}
