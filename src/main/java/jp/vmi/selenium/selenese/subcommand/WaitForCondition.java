package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.TimeoutException;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class WaitForCondition extends AbstractSubCommand<Void> {

    private static final long DEFAULT_INTERVAL = 500L;

    private static final int ARG_SCRIPT = 0;
    private static final int ARG_TIMEOUT = 1;

    /**
     * Constructor.
     */
    public WaitForCondition() {
        super(VALUE, VALUE);
    }

    private boolean until(Context context, String script) {
        Object result = context.getEval().eval(context.getWrappedDriver(), script);
        if (result == null)
            return false;
        else if (result instanceof String)
            return !((String) result).isEmpty();
        else if (result instanceof Boolean)
            return (Boolean) result;
        else
            return true;
    }

    @Override
    public Void execute(final Context context, String... args) {
        String script = args[ARG_SCRIPT];
        long timeout = Long.valueOf(args[ARG_TIMEOUT]);

        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            if (until(context, script))
                return null;
            try {
                Thread.sleep(DEFAULT_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new TimeoutException("Failed to resolve " + script);
    }
}
