package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.result.Failure;
import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.*;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "EditContent".
 * Not supported officially by Selenium IDE as of 23/11/2017
 * But present and useable in their new port of SideeX
 */
public class EditContent extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_VALUE = 1;

    EditContent(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        String value = curArgs[ARG_VALUE];

        WebDriver driver = context.getWrappedDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = context.getElementFinder().findElement(driver, locator);
        String javascript_escaped = StringEscapeUtils.escapeJavaScript(value);
        try {
            js.executeScript("var el = arguments[0]; el.innerHTML = '"+javascript_escaped+"';",element);
            return SUCCESS;
        } catch (JavascriptException e) {
            return new Failure(e.getMessage());
        }
    }
}
