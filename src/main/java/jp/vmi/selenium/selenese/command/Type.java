package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.ModifierKeyState;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "type".
 */
public class Type extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_VALUE = 1;

    Type(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        ModifierKeyState state = context.getModifierKeyState();
        if (state.isControlKeyDown() || state.isAltKeyDown() || state.isMetaKeyDown())
            throw new SeleneseRunnerRuntimeException(
                "type not supported immediately after call to controlKeyDown() or altKeyDown() or metaKeyDown()");
        String locator = curArgs[ARG_LOCATOR];
        String value = curArgs[ARG_VALUE];
        if (state.isShiftKeyDown())
            value = value.toUpperCase();
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        if (context.isReplaceAlertMethod())
            context.getJSLibrary().replaceAlertMethod(driver, element);
        String tagName = element.getTagName().toLowerCase();
        Result result = SUCCESS;
        switch (tagName) {
        case "input":
            String type = element.getAttribute("type");
            if (type != null && "file".equalsIgnoreCase(type))
                result = new Warning("You should be using attachFile to set the value of a file input element");
            element.clear();
            element.sendKeys(value);
            break;
        default:
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", element);
            element.sendKeys(value);
            break;
        }
        context.getJSLibrary().fireEvent(driver, element, "change");
        return result;
    }
}
