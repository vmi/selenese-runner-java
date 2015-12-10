package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Super class for DoCommand interceptor.
 */
abstract public class AbstractDoCommandInterceptor implements MethodInterceptor {

    private static final int CONTEXT = 0;
    private static final int COMMAND = 1;
    private static final int CUR_ARGS = 2;

    /*
     * target signature:
     * Result doCommand(Context context, ICommand command, String... curArgs)
     */
    @Override
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Context context = (Context) args[CONTEXT];
        ICommand command = (ICommand) args[COMMAND];
        String[] curArgs = (String[]) args[CUR_ARGS];
        return invoke(invocation, context, command, curArgs);
    }

    abstract protected Result invoke(MethodInvocation invocation, Context context, ICommand command, String[] curArgs) throws Throwable;
}
