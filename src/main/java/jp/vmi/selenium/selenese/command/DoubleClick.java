package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "doubleClick".
 */
public class DoubleClick extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    DoubleClick(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        WebDriver driver = context.getWrappedDriver();
        boolean isRetryable = !context.getCurrentTestCase().getSourceType().isSelenese();
        int timeout = context.getTimeout(); /* ms */
        WebElement element = context.getElementFinder().findElementWithTimeout(driver, locator, isRetryable, timeout);
        context.getJSLibrary().replaceAlertMethod(driver, element);
        new Actions(driver).doubleClick(element).perform();
        return SUCCESS;
    }
}
