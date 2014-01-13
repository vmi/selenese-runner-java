package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Runner;
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
        super(index, name, args, 2);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        testCase.addToCollection(args[COLLECTION_NAME], runner.getVarsMap().replaceVars(args[VALUE]));
        return SUCCESS;
    }
}
