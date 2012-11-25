package jp.vmi.selenium.selenese.command;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

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
        CustomCommandProcessor proc = testCase.getProc();
        boolean found = true;
        String message = null;
        int timeout = testCase.getRunner().getTimeout();
        int retryCount = timeout / RETRY_INTERVAL;
        for (int i = 0; i < retryCount; i++) {
            found = true;
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
                try {
                    Object result = proc.execute(getter, getterArgs);
                    String resultString = (result != null) ? result.toString() : "";
                    String expected = testCase.getProc().replaceVars(this.expected);
                    if (matches(resultString, expected) ^ isInverse)
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
                    boolean result = proc.getBoolean(getter, getterArgs);
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
            if (type != Type.WAIT_FOR)
                break;
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

    private boolean matches(String resultString, String expected) {
        String[] pattern = expected.split(":", 2);
        if (pattern.length == 2) {
            if ("regexp".equals(pattern[0]))
                return regexpMatches(resultString, pattern[1]);
            else if ("regexpi".equals(pattern[0]))
                return regexpMatches(resultString, pattern[1], Pattern.CASE_INSENSITIVE);
            else if ("exact".equals(pattern[0]))
                return StringUtils.equals(resultString, pattern[1]);
            else if ("glob".equals(pattern[0]))
                expected = pattern[1];
        }
        return globMatches(resultString, expected);
    }

    private boolean regexpMatches(String resultString, String expected, int flags) {
        Pattern p = Pattern.compile(expected, flags);
        Matcher m = p.matcher(resultString);
        return m.find();
    }

    private boolean regexpMatches(String resultString, String expected) {
        return regexpMatches(resultString, expected, 0);
    }

    private boolean globMatches(String resultString, String expected) {
        org.apache.oro.text.regex.Pattern p;
        try {
            p = new GlobCompiler().compile(expected);
        } catch (MalformedPatternException e) {
            throw new SeleniumException("malformed glob pattern:" + expected, e);
        }
        PatternMatcher m = new Perl5Matcher();
        return m.matches(resultString, p);
    }
}
