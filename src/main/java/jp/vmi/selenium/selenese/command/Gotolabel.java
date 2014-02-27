package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "gotolabel".
 */
public class Gotolabel extends AbstractCommand {

    private static final int ARG_LABEL = 0;

    Gotolabel(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getCommandListIterator().jumpTo(curArgs[ARG_LABEL]);
        return SUCCESS;
    }
}
