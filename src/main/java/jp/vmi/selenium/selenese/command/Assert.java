package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.subcommand.GetExpression;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "assert".
 */
public class Assert extends AbstractCommand {

    private static final int ARG_VAR_NAME = 0;
    private static final int ARG_VALUE = 1;

    private static final GetExpression getExpression = new GetExpression();
    private final Assertion assertExpression;

    Assert(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
        assertExpression = new Assertion(index, name, args, "assert", getExpression, false, false);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    private boolean isExpected(Object value, String expected) {
        String vStr = value == null ? "null" : value.toString();
        if (value instanceof Number && vStr.endsWith(".0"))
            vStr = vStr.substring(0, vStr.length() - 2);
        return vStr.equals(expected);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (context.getCurrentTestCase().getSourceType() == SourceType.SELENESE)
            return assertExpression.execute(context, curArgs);
        String varName = curArgs[ARG_VAR_NAME];
        Object value = context.getVarsMap().get(varName);
        String expected = curArgs[ARG_VALUE];
        if (isExpected(value, expected))
            return SUCCESS;
        else
            return new Failure(String.format("Assertion failed (Result: [%s] / Expected: [%s])", value, expected));
    }
}
