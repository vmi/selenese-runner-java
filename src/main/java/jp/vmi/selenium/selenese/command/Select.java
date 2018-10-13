package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "select".
 */
public class Select extends AbstractCommand {

    private static final int ARG_SELECT_LOCATOR = 0;
    private static final int ARG_OPTION_LOCATOR = 1;

    Select(int index, String name, String... args) {
        super(index, name, args, LOCATOR, OPTION_LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        SelectElement select = new SelectElement(context, curArgs[ARG_SELECT_LOCATOR]);
        if (select.isMultiple)
            select.unsetOptions();
        return select.selectOptions(curArgs[ARG_OPTION_LOCATOR], true);
    }
}
