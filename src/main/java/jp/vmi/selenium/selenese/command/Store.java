package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import jp.vmi.selenium.selenese.Context;

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
    public Result doCommand(Context context) {
        String result = context.doCommand(getter, context.replaceVariables(getterArgs));
        context.setVariable(result, varName);
        return SUCCESS;
    }
}
