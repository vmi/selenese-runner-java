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
        Runner runner = testCase.getRunner();
        CustomCommandProcessor proc = testCase.getProc();
        proc.unhighlight();
        Command command = (Command) invocation.getArguments()[0];
        if (runner.isHighlight()) {
            int i = 0;
            for (String locator : command.getLocators())
                proc.highlight(locator, HighlightStyle.ELEMENT_STYLES[i++]);
        }
        Result result = (Result) invocation.proceed();
        return result;
    }

}
