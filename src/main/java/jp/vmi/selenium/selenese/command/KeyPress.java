package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.ModifierKeyState;
import jp.vmi.selenium.selenese.javascript.JSLibrary.KeyEventType;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "keyPress".
 * Deprecated in selenese
 */
@Deprecated
public class KeyPress extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_KEY_SEQUENCE = 1;

    KeyPress(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        String keySequence = curArgs[ARG_KEY_SEQUENCE];
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        ModifierKeyState keyState = context.getModifierKeyState();
        context.getJSLibrary().triggerKeyEvent(driver, element, KeyEventType.KEYPRESS, keySequence, keyState);
        return SUCCESS;
    }
}
