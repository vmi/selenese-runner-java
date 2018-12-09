package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "while".
 */
public class While extends BlockStartImpl {

    private static final int ARG_CONDITION = 0;

    While(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (!context.isTrue(curArgs[ARG_CONDITION])) {
            context.getCommandListIterator().jumpToNextOf(blockEnd);
            return new Success("Break");
        } else {
            return new Success("Continue");
        }
    }
}
