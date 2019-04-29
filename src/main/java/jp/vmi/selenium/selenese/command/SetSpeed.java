package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.selenese.utils.LangUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "setSpeed".
 */
public class SetSpeed extends AbstractCommand {

    private static final int ARG_SPEED = 0;

    SetSpeed(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String speed = curArgs[ARG_SPEED];
        if (LangUtils.isBlank(speed))
            return new Warning("the argument of setSpeed is ignored: empty.");
        try {
            context.setSpeed(Long.parseLong(speed));
            return SUCCESS;
        } catch (NumberFormatException e) {
            return new Warning("the argument of setSpeed is ignored: invalid number format: " + speed);
        }
    }
}
