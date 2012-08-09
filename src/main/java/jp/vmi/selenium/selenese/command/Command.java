package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.selenium.selenese.result.Success.*;

// https://github.com/davehunt/selenium-ide-flowcontrol
public class Command {

    private final int index;
    protected final String name;
    protected final String[] args;
    protected Command next = null;

    /**
     * constructor.
     * 
     * @param index index number of Command
     * @param name selenese command
     * @param args command arguments.
     */
    Command(int index, String name, String... args) {
        this.index = index;
        this.name = name;
        this.args = args;
    }

    /**
     * execute selenese command.
     *
     * @param testCase
     * @return true if command terminated normally.
     */
    public Result doCommand(TestCase testCase) {
        return SUCCESS;
    }

    public Command setNext(Command next) {
        return this.next = next;
    }

    public Command next(TestCase testCase) {
        return next;
    }

    @Override
    public String toString() {
        return "Command#" + index + ": " + name + "(" + StringUtils.join(LoggerUtils.quote(args), ", ") + ")";
    }

    public int getIndex() {
        return index;
    }
}
