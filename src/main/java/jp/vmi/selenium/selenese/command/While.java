package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "while".
 */
public class While extends AbstractCommand implements StartLoop {

    private static final int ARG_CONDITION = 0;

    private EndWhile endLoop;

    While(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void setEndLoop(EndLoop endLoop) {
        this.endLoop = (EndWhile) endLoop;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (!context.isTrue(curArgs[ARG_CONDITION]))
            context.getCommandListIterator().jumpToNextOf(endLoop);
        return SUCCESS;
    }
}
