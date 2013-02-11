package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor.*;

/**
 * Command "store".
 */
public class Store extends Command {

    private final String getter;
    private final String[] getterArgs;
    private final String varName;

    Store(int index, String name, String[] args, String getter) {
        super(index, name, args, getArgumentCount(getter) + 1, getLocatorIndexes(getter));
        args = this.args;
        this.getter = getter;
        int len = args.length;
        getterArgs = Arrays.copyOf(args, len - 1);
        varName = args[len - 1];
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        CustomCommandProcessor proc = testCase.getProc();
        Object result = proc.execute(getter, getterArgs);
        proc.setVar(result, varName);
        return new Success(String.valueOf(result));
    }
}
