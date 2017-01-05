package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * An implementation of the "openWindow" method from Selenium.
 */
public class OpenWindow extends AbstractCommand {

    private static final int ARG_URL = 0;
    private static final int ARG_WINDOW_ID = 1;

    OpenWindow(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String script = String.format("window.open('%s', '%s'); null;", curArgs[ARG_URL], curArgs[ARG_WINDOW_ID]);
        context.getEval().eval(context, script);
        return SUCCESS;
    }
}
