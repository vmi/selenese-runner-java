package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Runner;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

/**
 * Command "echo".
 */
public class Echo extends Command {

    private static final int MESSAGE = 0;

    Echo(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        return new Success(testCase.getProc().replaceVars(args[MESSAGE]));
    }
}
