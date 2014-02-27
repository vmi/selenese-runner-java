package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;

/**
 *
 */
public class HighlightInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Context context = (Context) args[0];
        ICommand command = (ICommand) args[1];
        String[] curArgs = (String[]) args[2];
        if (context instanceof Runner) {
            Runner runner = (Runner) context;
            runner.unhighlight();
            if (runner.isHighlight()) {
                int i = 0;
                String[] locators = command.convertLocators(curArgs);
                for (String locator : locators)
                    runner.highlight(locator, HighlightStyle.ELEMENT_STYLES[i++]);
            }
        }
        return invocation.proceed();
    }
}
