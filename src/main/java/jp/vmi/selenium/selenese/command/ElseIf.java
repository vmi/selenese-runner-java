package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "if".
 */
public class ElseIf extends BlockStartImpl implements BlockEnd {

    private static final int ARG_CONDITION = 0;
    private boolean isSkippedNextBlock = false;

    ElseIf(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (getBlockStart().isSkippedNextBlock()) {
            isSkippedNextBlock = true;
            context.getCommandListIterator().jumpToNextOf(blockEnd);
            return new Success("Skip else if");
        } else if (context.isTrue(curArgs[ARG_CONDITION])) {
            isSkippedNextBlock = true;
            return new Success("True");
        } else {
            isSkippedNextBlock = false;
            context.getCommandListIterator().jumpToNextOf(blockEnd);
            return new Success("False");
        }
    }

    @Override
    public boolean isSkippedNextBlock() {
        return isSkippedNextBlock;
    }
}
