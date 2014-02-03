package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
import jp.vmi.selenium.selenese.cmdproc.HighlightStyle;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class HighlightInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TestCase testCase = (TestCase) invocation.getThis();
        Object[] args = invocation.getArguments();
        Command command = (Command) args[0];
        Runner runner = (Runner) args[1];
        CustomCommandProcessor proc = testCase.getProc();
        proc.unhighlight();
        if (runner.isHighlight()) {
            int i = 0;
            String[] locators = proc.replaceVarsForArray(command.getLocators());
            for (String locator : locators)
                proc.highlight(locator, HighlightStyle.ELEMENT_STYLES[i++]);
        }
        Result result = (Result) invocation.proceed();
        return result;
    }
}
