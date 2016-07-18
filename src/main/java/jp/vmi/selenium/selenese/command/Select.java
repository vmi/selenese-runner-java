package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "select".
 */
public class Select extends AbstractCommand {

    private static final int ARG_SELECT_LOCATOR = 0;
    private static final int ARG_OPTION_LOCATOR = 1;

    Select(int index, String name, String... args) {
        super(index, name, args, LOCATOR, OPTION_LOCATOR);
    }

    private boolean isMultiple(WebElement select) {
        String multiple = select.getAttribute("multiple");
        return multiple != null && (multiple.equalsIgnoreCase("true") || multiple.equalsIgnoreCase("multiple"));
    }

    private void unsetOptions(WebElement select) {
        for (WebElement option : select.findElements(By.tagName("option")))
            if (option.isSelected())
                option.click();
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String selectLocator = curArgs[ARG_SELECT_LOCATOR];
        String optionLocator = WebDriverElementFinder.convertToOptionLocatorWithParent(selectLocator, curArgs[ARG_OPTION_LOCATOR]);
        WebDriver driver = context.getWrappedDriver();
        WebDriverElementFinder finder = context.getElementFinder();
        WebElement select = finder.findElement(driver, selectLocator);
        context.getDialogOverride().replaceAlertMethod(driver, select);
        if (isMultiple(select))
            unsetOptions(select);
        WebElement option = finder.findElement(driver, optionLocator);
        if (!option.isSelected())
            option.click();
        return SUCCESS;
    }
}
