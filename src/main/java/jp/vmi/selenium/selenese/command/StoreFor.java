package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "storeFor".
 */
public class StoreFor extends AbstractCommand implements StartLoop {

    private static final int ARG_COLLECTION_NAME = 0;
    private static final int ARG_VAR_NAME = 1;

    private EndFor endLoop;

    StoreFor(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void setEndLoop(EndLoop endLoop) {
        this.endLoop = (EndFor) endLoop;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String value = context.getCollectionMap().pollFromCollection(curArgs[ARG_COLLECTION_NAME]);
        if (value == null)
            context.getCommandListIterator().jumpToNextOf(endLoop);
        else
            context.getVarsMap().put(curArgs[ARG_VAR_NAME], value);
        return SUCCESS;
    }
}
