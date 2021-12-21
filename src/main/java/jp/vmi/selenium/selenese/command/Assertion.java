package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Commands of "assert*", "verify*", and "waitFor*".
 */
public class Assertion extends AbstractCommand {

    private static final Logger log = LoggerFactory.getLogger(Assertion.class);

    private static final int RETRY_INTERVAL = 100 /* ms */;

    private enum Type {
        ASSERT("assert"), // throw exception
        VERIFY("verify"), // logging only
        WAIT_FOR("waitFor"); // wait timeout

        private String assertion;

        private Type(String assertion) {
            this.assertion = assertion;
        }

        public static Type of(String assertion) {
            for (Type type : values())
                if (type.assertion.equals(assertion))
                    return type;
            return null;
        }
    }

    private final Type type;
    private final ISubCommand<?> getterSubCommand;
    private final boolean isBoolean;
    private final boolean isInverse;

    private static ArgumentType[] getArgumentTypes(String assertion, ISubCommand<?> getterSubCommand, boolean isBoolean) {
        ArgumentType[] argTypes = getterSubCommand.getArgumentTypes();
        if (Type.WAIT_FOR.assertion.equals(assertion) || !isBoolean)
            return ArrayUtils.add(argTypes, VALUE);
        else
            return argTypes;
    }

    Assertion(int index, String name, String[] args, String assertion, ISubCommand<?> getterSubCommand, boolean isBoolean,
        boolean isInverse) {
        super(index, name, args, getArgumentTypes(assertion, getterSubCommand, isBoolean));
        this.type = Type.of(assertion);
        this.getterSubCommand = getterSubCommand;
        this.isBoolean = isBoolean;
        this.isInverse = isInverse;
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    private int getTimeout(Context context, String[] args) {
        if (type != Type.WAIT_FOR || args.length < 2)
            return context.getTimeout();
        int timeout = NumberUtils.toInt(args[1]);
        return timeout > 0 ? timeout : context.getTimeout();
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String[] getterArgs;
        String expected;
        if (isBoolean) {
            getterArgs = curArgs;
            expected = null;
        } else {
            int newLen = getterSubCommand.getArgumentTypes().length;
            getterArgs = Arrays.copyOf(curArgs, newLen);
            expected = curArgs[newLen];
        }
        boolean found = true;
        String message = null;
        int timeout = getTimeout(context, curArgs);
        long breakAfter = System.currentTimeMillis() + timeout;
        while (true) {
            found = true;
            if (isBoolean) {
                try {
                    boolean result = (Boolean) getterSubCommand.execute(context, getterArgs);
                    if (result ^ isInverse)
                        return SUCCESS;
                    message = String.format("Assertion failed (Result: [%s] / Expected: [%s])", result, !result);
                } catch (NotFoundException | StaleElementReferenceException e) {
                    if (isInverse)
                        return SUCCESS;
                    message = String.format("Assertion failed (%s)", e.getMessage());
                    found = false;
                }
            } else {
                try {
                    String resultString = SeleniumUtils.convertToString(getterSubCommand.execute(context, getterArgs));
                    if (SeleniumUtils.patternMatches(expected, resultString) ^ isInverse)
                        return SUCCESS;
                    message = String.format("Assertion failed (Result: [%s] / %sExpected: [%s])",
                        StringEscapeUtils.escapeJava(resultString),
                        isInverse ? "Not " : "", StringEscapeUtils.escapeJava(expected));
                } catch (NotFoundException | StaleElementReferenceException e) {
                    if (isInverse)
                        return SUCCESS;
                    message = String.format("Assertion failed (%s)", e.getMessage());
                    found = false;
                }
            }
            if (type != Type.WAIT_FOR || System.currentTimeMillis() > breakAfter)
                break;
            try {
                Thread.sleep(RETRY_INTERVAL);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
                break;
            }
        }
        switch (type) {
        case ASSERT:
            return new Failure(message);
        case VERIFY:
            return found ? new Warning(message) : new Failure(message);
        default: // == WAIT_FOR
            String m = String.format("Timed out after %dms (%s)", timeout, message);
            if (getSideCommand() != null)
                return new Failure(m);
            else
                return new Warning(m);
        }
    }

    @Override
    public int getArgumentCount() {
        int count = getterSubCommand.getArgumentCount();
        if (!isBoolean)
            count++;
        if (type == Type.WAIT_FOR)
            count++;
        return count;
    }
}
