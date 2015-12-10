package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Interceptor for logging and recoding test-case result.
 */
abstract public class AbstractExecuteTestCaseInterceptor implements MethodInterceptor {

    private static final int PARENT = 0;
    private static final int CONTEXT = 1;

    /*
     * target signature:
     * Result ITestCase#execute(Selenese parent, Context context)
     */
    @Override
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        ITestCase testCase = (ITestCase) invocation.getThis();
        Object[] args = invocation.getArguments();
        Selenese parent = (Selenese) args[PARENT];
        Context context = (Context) args[CONTEXT];
        return invoke(invocation, testCase, parent, context);
    }

    abstract protected Result invoke(MethodInvocation invocation, ITestCase testCase, Selenese parent, Context context) throws Throwable;
}
