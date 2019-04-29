package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.selenese.utils.LangUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "setTimeout".
 */
public class SetTimeout extends AbstractCommand {

    private static final int ARG_TIMEOUT = 0;

    SetTimeout(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String timeout = curArgs[ARG_TIMEOUT];
        if (LangUtils.isBlank(timeout))
            return new Warning("the argument of setTimeout is ignored: empty.");
        try {
            context.setTimeout(Integer.parseInt(timeout));
            return SUCCESS;
        } catch (NumberFormatException e) {
            return new Warning("the argument of setTimeout is ignored: invalid number format: " + timeout);
        }
    }
}
