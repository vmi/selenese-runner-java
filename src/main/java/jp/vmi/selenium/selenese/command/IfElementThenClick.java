package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "IfElementThenClick".
 */
public class IfElementThenClick extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    IfElementThenClick(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        context.getJSLibrary().replaceAlertMethod(driver, element);
        try {
            element.click();
            return SUCCESS;
        } catch (ElementNotVisibleException e) {
            context.executeScript("arguments[0].click()", element);
            return new Success("Success (the element is not visible)");
        }
    }
}
