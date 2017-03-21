package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static jp.vmi.selenium.selenese.command.ArgumentType.LOCATOR;
import static jp.vmi.selenium.selenese.command.ArgumentType.VALUE;
import static jp.vmi.selenium.selenese.result.Success.SUCCESS;

/**
 * Command "typeRandomValue".
 */
public class TypeRandomValue extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    TypeRandomValue(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        // Need to implement ability to pass the length of value to 'Random' method
        String value = RandomStringUtils.random(10, true, false);

        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        context.getJSLibrary().replaceAlertMethod(driver, element);

        element.sendKeys(value);

        Result result = SUCCESS;
        context.getJSLibrary().fireEvent(driver, element, "change");
        return result;
    }
}
