package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.StartLoop.*;
import static jp.vmi.selenium.selenese.result.Success.*;
import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * Abstract class for implementing selenese command.
 *
 * @deprecated use {@link AbstractCommand}.
 */
@Deprecated
public abstract class Command implements ICommand {

    private static final Command NEXT = new Command(-1, "dummy", ArrayUtils.EMPTY_STRING_ARRAY, 0) {
    };

    private final int index;
    protected final String name;
    protected final String[] args;
    private final int[] locatorIndexes;
    protected final String[] locators;
    private Result result = UNEXECUTED;
    private StartLoop startLoop = NO_START_LOOP;
    private List<Screenshot> screenshots = null;

    /**
     * Constructor.
     *
     * @param index index number of Command. (1 origin)
     * @param name selenese command name.
     * @param args command arguments.
     * @param argCnt argument count. (less than or equals to args.length)
     * @param locatorIndexes locator indexes. (0 origin. use NO_LOCATOR_INDEX if no locator)
     */
    public Command(int index, String name, String[] args, int argCnt, int[] locatorIndexes) {
        this.index = index;
        this.name = name;
        this.args = (args.length == argCnt) ? args : Arrays.copyOf(args, argCnt);
        this.locatorIndexes = locatorIndexes;
        this.locators = new String[locatorIndexes.length];
        int i = 0;
        for (int ndx : locatorIndexes)
            this.locators[i++] = args[ndx];
    }

    /**
     * Constructor. (for command with no locator)
     *
     * @param index index number of Command. (1 origin)
     * @param name selenese command name.
     * @param args command arguments.
     * @param argCnt argument count. (less than or equals to args.length)
     */
    public Command(int index, String name, String[] args, int argCnt) {
        this(index, name, args, argCnt, ArrayUtils.EMPTY_INT_ARRAY);
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
    @Override
    public final int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
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
    @Override
    public final Result getResult() {
        return result;
    }

    /**
     * Set result of this command.
     *
     * @param result result of this command.
     * @return result itself.
     */
    protected final Result setResult(Result result) {
        return this.result = result;
    }

    /**
     * Get source elements.
     *
     * @return array of source elements. (always 3 elements)
     */
    @Override
    public final String[] getSource() {
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

    /**
     * Implementation of command.
     * You can override this method.
     *
     * @param testCase test-case instance.
     * @param runner Runner object.
     * @return result.
     */
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
     * Get next command.
     *
     * @param testCase test-case instance.
     * @return next command.
     */
    @Deprecated
    public Command next(TestCase testCase) {
        return NEXT;
    }

    @Override
    public void setStartLoop(StartLoop startLoop) {
        this.startLoop = startLoop;
    }

    @Override
    public StartLoop getStartLoop() {
        return startLoop;
    }

    @Override
    public String toString() {
        return AbstractCommand.toString(index, name, args);
    }

    @Override
    public boolean mayUpdateScreen() {
        return canUpdate();
    }

    @Override
    public String[] getArguments() {
        return args;
    }

    @Override
    public String[] convertLocators(String[] args) {
        if (locatorIndexes.length == 0)
            return ArrayUtils.EMPTY_STRING_ARRAY;
        String[] locators = new String[locatorIndexes.length];
        int i = 0;
        for (int locatorIndex : locatorIndexes)
            locators[i++] = args[locatorIndex];
        return locators;
    }

    @Override
    public final Result execute(Context context, String... curArgs) {
        Runner runner = (Runner) context;
        TestCase testCase = runner.getCurrentTestCase();
        try {
            result = doCommandImpl(testCase, runner);
        } catch (RuntimeException e) {
            result = new Error(e);
        }
        ICommand next = next(testCase);
        if (next != NEXT)
            runner.getCommandListIterator().jumpTo(null);
        return result;
    }

    @Override
    public void addScreenshot(String path, String label) {
        if (path == null)
            return;
        if (screenshots == null)
            screenshots = new ArrayList<Screenshot>();
        screenshots.add(new Screenshot(path, label));
    }

    @Override
    public List<Screenshot> getScreenshots() {
        if (screenshots == null)
            return Collections.emptyList();
        return screenshots;
    }
}
