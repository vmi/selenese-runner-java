package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "store".
 */
public class Store extends AbstractCommand {

    private final ISubCommand<?> getterSubCommand;

    Store(int index, String name, String[] args, ISubCommand<?> getterSubCommand) {
        super(index, name, args, ArrayUtils.add(getterSubCommand.getArgumentTypes(), VALUE));
        this.getterSubCommand = getterSubCommand;
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        int argLen = getterSubCommand.getArgumentTypes().length;
        String varName = curArgs[argLen];
        Object result = getterSubCommand.execute(context, Arrays.copyOf(curArgs, argLen));
        context.getVarsMap().put(varName, result);
        return new Success(SeleniumUtils.convertToString(result));
    }

    @Override
    public int getArgumentCount() {
        return getterSubCommand.getArgumentCount() + 1;
    }
}
