package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "executeScript".
 */
public class ExecuteScript extends AbstractCommand {

    private static final int ARG_SCRIPT = 0;
    private static final int ARG_VAR_NAME = 1;

    ExecuteScript(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        Object result = context.executeScript(curArgs[ARG_SCRIPT]);
        if (curArgs.length == 2) {
            String varName = curArgs[ARG_VAR_NAME];
            if (!varName.isEmpty())
                context.getVarsMap().put(varName, result);
        }
        return new Success(SeleniumUtils.convertToString(result));
    }
}
