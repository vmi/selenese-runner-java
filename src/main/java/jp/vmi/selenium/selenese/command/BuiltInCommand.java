package jp.vmi.selenium.selenese.command;

import com.google.common.base.Strings;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Commands implemented by WebDriverCommandProcessor or ISubCommand.
 */
public class BuiltInCommand extends AbstractCommand {

    private final ISubCommand<?> subCommand;
    private final boolean andWait;
    private final boolean mayUpdateScreen;

    BuiltInCommand(int index, String name, String[] args, ISubCommand<?> subCommand, boolean andWait) {
        super(index, name, args, subCommand.getArgumentTypes());
        this.subCommand = subCommand;
        this.andWait = andWait;
        this.mayUpdateScreen = true;
    }

    @Override
    public boolean mayUpdateScreen() {
        return mayUpdateScreen;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String resultString = SeleniumUtils.convertToString(subCommand.execute(context, curArgs));
        if (andWait) {
            int timeout = context.getTimeout();
            WaitForPageToLoad.execute(context, timeout);
        }
        return Strings.isNullOrEmpty(resultString) ? SUCCESS : new Success(resultString);
    }

    @Override
    public int getArgumentCount() {
        return subCommand.getArgumentCount();
    }
}
