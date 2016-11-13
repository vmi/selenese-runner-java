package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.JavascriptExecutor;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of GetExpression.
 */
public class GetExpression extends AbstractSubCommand<Object> {

    private static final int ARG_EXPR = 0;

    /**
     * Constructor.
     */
    public GetExpression() {
        super(ArgumentType.VALUE);
    }

    @Override
    public Object execute(Context context, String... args) {
        return ((JavascriptExecutor) context.getWrappedDriver()).executeScript(args[ARG_EXPR]);
    }
}
