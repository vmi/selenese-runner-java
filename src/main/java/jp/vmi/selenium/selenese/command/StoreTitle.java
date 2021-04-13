package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "storeTitle".
 */
public class StoreTitle extends AbstractCommand {

    private static final ArgumentType[] ARG_TYPES = new ArgumentType[] { VALUE, VALUE };
    private final ISubCommand<?> getterSubCommand;

    StoreTitle(int index, String name, String[] args, ISubCommand<?> getterSubCommand) {
        super(index, name, args, ARG_TYPES);
        this.getterSubCommand = getterSubCommand;
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String varName;
        switch (curArgs.length) {
        case 0:
            throw new SeleneseRunnerRuntimeException("Missing variable name");
        case 1:
            varName = curArgs[0];
            break;
        case 2:
            String text = curArgs[0];
            varName = curArgs[1];
            if (varName == null || varName.isEmpty()) {
                varName = text;
            } else if (text != null && !text.isEmpty()) {
                // This behavior is compatible with the Selenium IDE.
                context.getVarsMap().put(varName, text);
                return new Success(text);
            }
            break;
        default:
            // don't reach here.
            throw new SeleneseRunnerRuntimeException("Don't support 3 or more arguments.");
        }
        Object result = getterSubCommand.execute(context);
        context.getVarsMap().put(varName, result);
        return new Success(SeleniumUtils.convertToString(result));
    }

    @Override
    public int getArgumentCount() {
        return ARG_TYPES.length;
    }
}
