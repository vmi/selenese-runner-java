package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Abstract class for implementing selenese command.
 */
public abstract class Command {

    private final int index;
    protected final String name;
    protected final String[] args;
    protected Command next = null;

    /**
     * Constructor.
     *
     * @param index index number of Command (1 origin).
     * @param name selenese command name.
     * @param args command arguments.
     */
    Command(int index, String name, String[] args, int argCnt) {
        this.index = index;
        this.name = name;
        this.args = (args.length == argCnt) ? args : Arrays.copyOf(args, argCnt);
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
     * Execute selenese command.
     *
     * @param testCase test-case instatnce.
     * @return true if command terminated normally.
     */
    public Result doCommand(TestCase testCase) {
        return SUCCESS;
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
