package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "addToCollection".
 */
public class AddToCollection extends AbstractCommand {

    private static final int ARG_COLLECTION_NAME = 0;
    private static final int ARG_VALUE_TO_ADDING = 1;

    AddToCollection(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getCollectionMap().addToCollection(curArgs[ARG_COLLECTION_NAME], curArgs[ARG_VALUE_TO_ADDING]);
        return SUCCESS;
    }
}
