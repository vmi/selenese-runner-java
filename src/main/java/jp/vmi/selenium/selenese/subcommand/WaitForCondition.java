package jp.vmi.selenium.selenese.subcommand;

import com.thoughtworks.selenium.Wait;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class WaitForCondition extends AbstractSubCommand<Void> {

    private static final int ARG_SCRIPT = 0;
    private static final int ARG_TIMEOUT = 1;

    /**
     * Constructor.
     */
    public WaitForCondition() {
        super(VALUE, VALUE);
    }

    @Override
    public Void execute(final Context context, String... args) {
        final String script = args[ARG_SCRIPT];
        final String timeout = args[ARG_TIMEOUT];
        new Wait() {
            @Override
            public boolean until() {
                Object result = context.getEval().eval(context.getWrappedDriver(), script);
                if (result == null) {
                    return false;
                } else if (result instanceof String) {
                    return !"".equals(result);
                } else if (result instanceof Boolean) {
                    return (Boolean) result;
                } else {
                    return true;
                }
            }
        }.wait("Failed to resolve " + script, Long.valueOf(timeout));
        return null;
    }
}
