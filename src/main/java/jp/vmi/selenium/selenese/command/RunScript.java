package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * An implementation of the "runScript" method from Selenium.
 */
public class RunScript extends AbstractCommand {

    private static final int ARG_SCRIPT = 0;

    /**
     * Constructor.
     *
     * @param eval evaluator.
     */
    RunScript(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getEval().eval(context, curArgs[ARG_SCRIPT]);
        return SUCCESS;
    }
}
