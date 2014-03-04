package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.highlight.HighlightHandler;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;

/**
 *
 */
public class HighlightInterceptor implements MethodInterceptor {

    private static final int CONTEXT = 0;
    private static final int COMMAND = 1;
    private static final int CUR_ARGS = 2;

    /*
     * target signature:
     * Result doCommand(Context context, ICommand command, String... curArgs)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Context context = (Context) args[CONTEXT];
        ICommand command = (ICommand) args[COMMAND];
        String[] curArgs = (String[]) args[CUR_ARGS];
        if (context instanceof HighlightHandler) {
            HighlightHandler handler = (HighlightHandler) context;
            handler.unhighlight();
            if (handler.isHighlight()) {
                int i = 0;
                String[] locators = command.convertLocators(curArgs);
                for (String locator : locators)
                    handler.highlight(locator, HighlightStyle.ELEMENT_STYLES[i++]);
            }
        }
        return invocation.proceed();
    }
}
