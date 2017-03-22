package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static jp.vmi.selenium.selenese.command.ArgumentType.LOCATOR;
import static jp.vmi.selenium.selenese.result.Success.SUCCESS;

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
//        WebDriverElementFinder finder = context.getElementFinder();
        try {
            WebElement element = context.getElementFinder().findElement(driver, locator);
            element.click();
        } catch (NoSuchElementException e) {
            return SUCCESS;
        }
        return SUCCESS;
    }
}
