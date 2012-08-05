package jp.vmi.selenium.selenese;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CommandLogInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object r = invocation.proceed();
        // TODO implement logging.
        return r;
    }

}
