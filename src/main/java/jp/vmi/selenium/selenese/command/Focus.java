package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "focus".
 */
public class Focus extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    Focus(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, curArgs[ARG_LOCATOR]);
        new Actions(driver).moveToElement(element).perform();
        return SUCCESS;
    }
}
