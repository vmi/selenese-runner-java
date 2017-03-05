package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of FireEvent.
 */
public class FireEvent extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_EVENT_NAME = 1;

    FireEvent(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        WebElement element = context.findElement(curArgs[ARG_LOCATOR]);
        context.getJSLibrary().fireEvent(context.getWrappedDriver(), element, curArgs[ARG_EVENT_NAME]);
        return SUCCESS;
    }
}
