package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "addToCollection".
 */
public class AddToCollection extends Command {

    private static final int COLLECTION_NAME = 0;
    private static final int VALUE = 1;

    AddToCollection(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 2, NO_LOCATOR_INDEX);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        testCase.addToCollection(args[COLLECTION_NAME], testCase.getProc().replaceVars(args[VALUE]));
        return SUCCESS;
    }
}
