package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.selenium.selenese.result.Success.*;
import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * Abstract class for implementing selenese command.
 */
public abstract class Command {

    private static final int[] NO_LOCATOR_INDEX = new int[0];

    private final int index;
    protected final String name;
    protected final String[] args;
    protected final String[] locators;
    protected Command next = null;
    private Result result = UNEXECUTED;

    /**
     * Constructor.
     *
     * @param index index number of Command (1 origin).
     * @param name selenese command name.
     * @param args command arguments.
     * @param argCnt argument count.
     * @param locatorIndexes locator indexes.
     */
    Command(int index, String name, String[] args, int argCnt, int[] locatorIndexes) {
        this.index = index;
        this.name = name;
        this.args = (args.length == argCnt) ? args : Arrays.copyOf(args, argCnt);
        this.locators = new String[locatorIndexes.length];
        int i = 0;
        for (int ndx : locatorIndexes)
            this.locators[i++] = args[ndx];
    }

    /**
     * Constructor.
     *
     * @param index index number of Command (1 origin).
     * @param name selenese command name.
     * @param args command arguments.
     * @param argCnt argument count.
     */
    Command(int index, String name, String[] args, int argCnt) {
        this(index, name, args, argCnt, NO_LOCATOR_INDEX);
    }

    /**
     * Has this command result?
     * <p>
     * For example, "echo" and "comment" have no result.
     * </p>
     * @return true if command has result.
     */
    public boolean hasResult() {
        return true;
    }

    /**
     * Can this command update screen?
     *
     * @return true if command can update screen.
     */
    public boolean canUpdate() {
        return true;
    }

    /**
     * Get index in selenese script file (1 origin).
     *
     * @return index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get locators of command.
     *
     * @return locators (array length is 0 if the command has no locator).
     */
    public String[] getLocators() {
        return locators;
    }

    /**
     * Get result of this command.
     * 
     * @return result of this command.
     */
    public Result getResult() {
        return result;
    }

    /**
     * Set result of this command.
     *
     * @param result result of this command.
     * @return result itself.
     */
    protected Result setResult(Result result) {
        return this.result = result;
    }

    /**
     * Get source elements.
     *
     * @return array of source elements. (always 3 elements)
     */
    public String[] getSource() {
        String[] source = new String[3];
        source[0] = name;
        switch (args.length) {
        case 3:
        case 2:
            source[2] = args[1];
            // fall through
        case 1:
            source[1] = args[0];
        default:
            break;
        }
        return source;
    }

    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        return SUCCESS;
    }

    /**
     * Execute selenese command.
     *
     * @param testCase test-case instatnce.
     * @param runner Runner object.
     * @return true if command terminated normally.
     */
    public final Result doCommand(TestCase testCase, Runner runner) {
        try {
            return setResult(doCommandImpl(testCase, runner));
        } catch (RuntimeException e) {
            setResult(new Error(e));
            throw e;
        }
    }

    /**
     * add command at end of command list.
     * @param next command.
     * @return same as next.
     */
    public Command setNext(Command next) {
        return this.next = next;
    }

    /**
     * Get next command.
     *
     * @param testCase test-case instance.
     * @return next command.
     */
    public Command next(TestCase testCase) {
        return next;
    }

    @Override
    public String toString() {
        return "Command#" + index + ": " + name + "(" + StringUtils.join(LoggerUtils.quote(args), ", ") + ")";
    }
}
