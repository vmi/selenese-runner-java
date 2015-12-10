package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Selenese;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Interceptor for logging and recoding test-suite result.
 */
abstract public class AbstractExecuteTestSuiteInterceptor implements MethodInterceptor {

    private static final int PARENT = 0;
    private static final int CONTEXT = 1;

    /*
     * Target signature:
     * Result TestSuite#execute(Selenese parent, Context context)
     */
    @Override
    public final Object invoke(MethodInvocation invocation) throws Throwable {
        TestSuite testSuite = (TestSuite) invocation.getThis();
        Object[] args = invocation.getArguments();
        Selenese parent = (Selenese) args[PARENT];
        Context context = (Context) args[CONTEXT];
        return invoke(invocation, testSuite, parent, context);
    }

    abstract protected Result invoke(MethodInvocation invocation, TestSuite testSuite, Selenese parent, Context context) throws Throwable;
}
