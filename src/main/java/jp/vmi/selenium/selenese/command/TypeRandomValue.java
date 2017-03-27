package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.VarsMap;
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
    private static final int ARG_VAR_NAME = 1;

    TypeRandomValue(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        String varName = curArgs[ARG_VAR_NAME];
        // Need to implement ability to pass the length of value to 'Random' method
        String value = RandomStringUtils.random(10, true, false);

        if (!varName.equals("")) {
            VarsMap varsMap = context.getVarsMap();
            varsMap.put(varName, value);
        }

        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        context.getJSLibrary().replaceAlertMethod(driver, element);

        element.sendKeys(value);
        context.getJSLibrary().fireEvent(driver, element, "change");
        return SUCCESS;
    }
}
