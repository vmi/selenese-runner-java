package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.Wait;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * An implementation of "waitForCondition".
 */
public class WaitForCondition extends AbstractCommand {

    private static final int ARG_SCRIPT = 0;
    private static final int ARG_TIMEOUT = 1;

    WaitForCondition(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(final Context context, String... curArgs) {
        final String script = curArgs[ARG_SCRIPT];
        long timeout = Long.valueOf(curArgs[ARG_TIMEOUT]);
        long startTime = System.currentTimeMillis();
        boolean waitResult = Wait.defaultInterval.wait(startTime, timeout, () -> {
            Object result = context.getEval().eval(context, script);
            if (result == null)
                return false;
            else if (result instanceof String)
                return !((String) result).isEmpty();
            else if (result instanceof Boolean)
                return (Boolean) result;
            else
                return true;
        });
        if (waitResult)
            return SUCCESS;
        else
            return new Error("Timed out after " + timeout + "ms");
    }
}
