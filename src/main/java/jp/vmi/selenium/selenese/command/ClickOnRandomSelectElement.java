package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.LOCATOR;
import static jp.vmi.selenium.selenese.command.ArgumentType.OPTION_LOCATOR;
import static jp.vmi.selenium.selenese.result.Success.SUCCESS;

/**
 * Selects random element from drop-down lists by the xpath locator. Works correctly with drop-down lists with ul/li elements.
 *
 /* @param locator an element locator should return the list of elements by xpath.
 */
public class ClickOnRandomSelectElement extends AbstractCommand{

    private static final int ARG_SELECT_LOCATOR = 0;
    private static final int ARG_OPTION_LOCATOR = 1;

    ClickOnRandomSelectElement(int index, String name, String... args) {
        super(index, name, args, LOCATOR, OPTION_LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        SelectElement select = new SelectElement(context, curArgs[ARG_SELECT_LOCATOR]);
        if (select.isMultiple)
            select.unsetOptions();
        select.selectOptions(curArgs[ARG_OPTION_LOCATOR], true);
        return SUCCESS;
    }
}



