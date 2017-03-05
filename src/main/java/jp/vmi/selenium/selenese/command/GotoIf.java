package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "gotoIf".
 */
public class GotoIf extends AbstractCommand {

    private static final int ARG_EXPRESSION = 0;
    private static final int ARG_LABEL = 1;

    GotoIf(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public Result executeImpl(Context context, String... curArgs) {
        if (context.isTrue(curArgs[ARG_EXPRESSION]))
            context.getCommandListIterator().jumpTo(curArgs[ARG_LABEL]);
        return SUCCESS;
    }
}
