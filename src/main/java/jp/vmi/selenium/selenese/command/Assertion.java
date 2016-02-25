package jp.vmi.selenium.selenese.command;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

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

    private static ArgumentType[] getArgumentTypesOfThisCommand(ArgumentType[] argTypes, boolean isBoolean) {
        return isBoolean ? argTypes : ArrayUtils.add(argTypes, VALUE);
    }

    Assertion(int index, String name, String[] args, String assertion, ISubCommand<?> getterSubCommand, boolean isBoolean,
        boolean isInverse) {
        super(index, name, args, getArgumentTypesOfThisCommand(getterSubCommand.getArgumentTypes(), isBoolean));
        this.type = Type.of(assertion);
        this.getterSubCommand = getterSubCommand;
        this.isBoolean = isBoolean;
        this.isInverse = isInverse;
        //        // "getAttribute" has a special locator argument.
        //        // Please check Store.java if want to modify following code.
        //        if ("getAttribute".equalsIgnoreCase(getterSubCommand.name)) {
        //            int at = locators[0].lastIndexOf('@');
        //            if (at >= 0)
        //                locators[0] = locators[0].substring(0, at);
        //        }
        //        if ("getCssCount".equalsIgnoreCase(getterSubCommand.name))
        //            cssLocator = new String[] { "css=" + args[0] };
        //        else
        //            cssLocator = null;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        final String[] getterArgs;
        final String expected;
        if (isBoolean) {
            getterArgs = curArgs;
            expected = null;
        } else {
            int newLen = getterSubCommand.getArgumentTypes().length;
            getterArgs = Arrays.copyOf(curArgs, newLen);
            expected = curArgs[newLen];
        }
        int timeout = context.getTimeout();
        switch (type) {
        case WAIT_FOR:
            final AtomicReference<String> message = new AtomicReference<String>("");
            final Context ctx = context;
            FutureTask<Result> future = new FutureTask<Result>(new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    while (true) {
                        Result state = executeOnceImpl(ctx, expected, getterArgs);
                        if (state == SUCCESS)
                            return state;
                        else
                            message.set(state.getMessage());
                        Thread.sleep(RETRY_INTERVAL);
                    }
                }
            });
            try {
                return future.get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
            } catch (ExecutionException e) {
                log.warn(e.getMessage());
            } catch (TimeoutException e) {
                log.warn(e.getMessage());
            }
            return new Warning(String.format("Timed out after %dms%s", timeout, message.get()));
        default: // == ASSERT or VERIFY
            return executeOnceImpl(context, expected, getterArgs);
        }
    }

    private Result executeOnceImpl(Context context, String expected, String... getterArgs) {
        boolean found = true;
        String message = null;
        if (isBoolean) {
            try {
                boolean result = (Boolean) getterSubCommand.execute(context, getterArgs);
                if (result ^ isInverse)
                    return SUCCESS;
                message = String.format("Assertion failed (Result: [%s] / Expected: [%s])", result, !result);
            } catch (SeleniumException e) {
                String error = e.getMessage();
                if (!error.endsWith(" not found"))
                    throw e;
                message = String.format("Assertion failed (%s)", error);
                found = false;
            }
        } else {
            try {
                String resultString = SeleniumUtils.convertToString(getterSubCommand.execute(context, getterArgs));
                if (SeleniumUtils.patternMatches(expected, resultString) ^ isInverse)
                    return SUCCESS;
                message = String.format("Assertion failed (Result: [%s] / %sExpected: [%s])",
                        resultString, isInverse ? "Not " : "", expected);
            } catch (SeleniumException e) {
                String error = e.getMessage();
                if (!error.endsWith(" not found"))
                    throw e;
                message = String.format("Assertion failed (%s)", error);
                found = false;
            }
        }
        switch (type) {
        case ASSERT:
            return new Failure(message);
        case VERIFY:
            return found ? new Warning(message) : new Failure(message);
        default: // == WAIT_FOR
            return new Warning(message != null ? String.format(" (%s)", message) : "");
        }
    }
}
