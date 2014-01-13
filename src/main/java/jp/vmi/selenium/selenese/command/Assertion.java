package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor.*;
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
    private final String[] cssLocator;

    Assertion(int index, String name, String[] args, String assertion, String getter, boolean isBoolean, boolean isInverse) {
        super(index, name, args, getArgumentCount(getter) + (isBoolean ? 0 : 1), getLocatorIndexes(getter));
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
        // "getAttribute" has a special locator argument.
        // Please check Store.java if want to modify following code.
        if ("getAttribute".equals(getter)) {
            int at = locators[0].lastIndexOf('@');
            if (at >= 0)
                locators[0] = locators[0].substring(0, at);
        }
        if (getter.equalsIgnoreCase("getCssCount"))
            cssLocator = new String[] { "css=" + args[0] };
        else
            cssLocator = null;
    }

    @Override
    public String[] getLocators() {
        return cssLocator != null ? cssLocator : super.getLocators();
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        SeleneseRunnerCommandProcessor proc = testCase.getProc();
        boolean found = true;
        String message = null;
        int timeout = runner.getTimeout();
        long breakAfter = System.currentTimeMillis() + timeout;
        while (true) {
            found = true;
            if (this.expected != null) {
                try {
                    String resultString = proc.convertToString(proc.execute(getter, getterArgs));
                    String expected = runner.getVarsMap().replaceVars(this.expected);
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
            } else {
                try {
                    boolean result = proc.execute(getter, getterArgs);
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
            return new Warning(String.format("Timed out after %dms (%s)", timeout, message));
        }
    }
}
