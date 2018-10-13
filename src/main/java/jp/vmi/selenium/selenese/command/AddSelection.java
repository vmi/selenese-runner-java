package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "addSelection".
 */
public class AddSelection extends AbstractCommand {

    private static final int ARG_SELECT_LOCATOR = 0;
    private static final int ARG_OPTION_LOCATOR = 1;

    AddSelection(int index, String name, String... args) {
        super(index, name, args, LOCATOR, OPTION_LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        SelectElement select = new SelectElement(context, curArgs[ARG_SELECT_LOCATOR]);
        if (!select.isMultiple)
            throw new SeleneseRunnerRuntimeException("You may only add a selection to a select that supports multiple selections");
        return select.selectOptions(curArgs[ARG_OPTION_LOCATOR], true);
    }
}
