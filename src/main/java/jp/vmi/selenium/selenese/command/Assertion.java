package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Commands of "assert*", "verify*", and "waitFor*".
 */
public class Assertion extends Command {

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
    private final String getter;
    private final String[] getterArgs;
    private final String expected;
    private final boolean isInverse;

    Assertion(int index, String name, String[] args, String assertion, String getter, boolean isBoolean, boolean isInverse) {
        super(index, name, args, CustomCommandProcessor.getArgumentCount(getter) + (isBoolean ? 0 : 1));
        args = this.args;
        type = Type.of(assertion);
        this.getter = getter;
        if (isBoolean) {
            getterArgs = args;
            expected = null;
        } else {
            int len = args.length;
            getterArgs = Arrays.copyOf(args, len - 1);
            expected = args[len - 1];
        }
        this.isInverse = isInverse;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        String message = null;
        int retryCount = testCase.getRunner().getTimeout() / RETRY_INTERVAL;
        for (int i = 0; i < retryCount; i++) {
            if (i != 0) {
                // don't wait before first test and after last test.
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    log.warn(e.getMessage());
                    break;
                }
            }
            if (this.expected != null) {
                Object result = testCase.doBuiltInCommand(getter, getterArgs);
                String resultString = (result != null) ? result.toString() : "";
                String expected = testCase.getProc().replaceVars(this.expected);
                if (StringUtils.equals(resultString, expected) ^ isInverse)
                    return SUCCESS;
                message = String.format("Assertion failed (Result: [%s] / %sExpected: [%s]", result, isInverse ? "Not " : "", expected);
            } else {
                boolean result = testCase.isBuiltInCommand(getter, getterArgs);
                if (result ^ isInverse)
                    return SUCCESS;
                message = String.format("Assertion failed (Result: [%s] / Expected: [%s]", result, !result);
            }
            if (type != Type.WAIT_FOR)
                break;
        }
        switch (type) {
        case ASSERT:
        case WAIT_FOR:
            return new Failure(message);
        default: // VERIFY
            return new Warning(message);
        }
    }
}
