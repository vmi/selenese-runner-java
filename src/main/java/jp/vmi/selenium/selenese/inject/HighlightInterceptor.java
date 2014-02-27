package jp.vmi.selenium.selenese.inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.result.Result;

/**
 *
 */
public class HighlightInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        Command command = (Command) args[0];
        Runner runner = (Runner) args[1];
        runner.unhighlight();
        if (runner.isHighlight()) {
            int i = 0;
            String[] locators = runner.getVarsMap().replaceVarsForArray(command.getLocators());
            if (locators.length == 2 && locators[1].matches("(index|label|value)=.*")) {
                String[] optLocators = new String[2];
                optLocators[0] = locators[0];
                optLocators[1] = WebDriverElementFinder.convertToOptionLocatorWithParent(locators[0], locators[1]);
                locators = optLocators;
            }
            for (String locator : locators)
                runner.highlight(locator, HighlightStyle.ELEMENT_STYLES[i++]);
        }
        Result result = (Result) invocation.proceed();
        return result;
    }
}
