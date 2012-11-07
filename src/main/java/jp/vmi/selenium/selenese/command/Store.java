package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

/**
 * Command "store".
 */
public class Store extends Command {

    private final String getter;
    private final String[] getterArgs;
    private final String varName;

    Store(int index, String name, String[] args, String getter) {
        super(index, name, args);
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
        Object result = testCase.doBuiltInCommand(getter, getterArgs);
        testCase.getProc().setVar(result, varName);
        return new Success(String.valueOf(result));
    }
}
