package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "addCollection".
 */
public class AddCollection extends AbstractCommand {

    private static final int ARG_COLLECTION_NAME = 0;

    AddCollection(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getCollectionMap().addCollection(curArgs[ARG_COLLECTION_NAME]);
        return SUCCESS;
    }
}
