package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "selectFrame".
 */
public class SelectFrame extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    SelectFrame(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        context.getElementFinder().selectFrame(context.getWrappedDriver(), locator);
        return SUCCESS;
    }
}
