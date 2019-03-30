package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "repeatIf".
 */
public class RepeatIf extends AbstractCommand implements BlockEnd {

    private static final int ARG_CONDITION = 0;

    RepeatIf(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (context.isTrue(curArgs[ARG_CONDITION])) {
            BlockStart blockStart = getBlockStart();
            int index = ((ICommand) blockStart).getIndex();
            context.getCommandListIterator().jumpTo(blockStart);
            return new Success("Repeat. Go to next loop (#" + index + ")");
        } else {
            return new Success("Break");
        }
    }
}
