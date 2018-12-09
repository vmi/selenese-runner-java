package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "if".
 */
public class If extends BlockStartImpl {

    private static final int ARG_CONDITION = 0;
    private boolean isSkippedNextBlock = false;

    If(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (context.isTrue(curArgs[ARG_CONDITION])) {
            isSkippedNextBlock = true;
            return new Success("True");
        } else {
            context.getCommandListIterator().jumpToNextOf(blockEnd);
            isSkippedNextBlock = false;
            return new Success("False");
        }
    }

    @Override
    public boolean isSkippedNextBlock() {
        return isSkippedNextBlock;
    }
}
