package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class GetEval extends AbstractSubCommand<Object> {

    private static final int ARG_SCRIPT = 0;

    /**
     * Constructor.
     */
    public GetEval() {
        super(VALUE);
    }

    @Override
    public Object execute(Context context, String... args) {
        return context.getEval().eval(context, args[ARG_SCRIPT]);
    }
}
