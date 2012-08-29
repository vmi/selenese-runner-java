package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "addCollection".
 */
public class AddCollection extends Command {

    private static final int COLLECTION_NAME = 0;

    AddCollection(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        testCase.addCollection(args[COLLECTION_NAME]);
        return SUCCESS;
    }
}
