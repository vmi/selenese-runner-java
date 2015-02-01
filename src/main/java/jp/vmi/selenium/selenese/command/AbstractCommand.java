package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.selenium.selenese.command.StartLoop.*;
import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * Base implementation of command.
 */
public abstract class AbstractCommand implements ICommand {

    private final int index;
    private final String name;
    private final String[] args;
    private final ArgumentType[] argTypes;
    private final int[] locatorIndexes;
    private Result result = UNEXECUTED;
    private StartLoop startLoop = NO_START_LOOP;
    private List<Screenshot> screenshots = null;

    /**
     * Constructor.
     *
     * @param index command index.
     * @param name command name.
     * @param args command args.
     * @param argTypes command argument types.
     */
    public AbstractCommand(int index, String name, String[] args, ArgumentType... argTypes) {
        this.index = index;
        this.name = name;
        int curLen = args.length;
        int reqLen = argTypes.length;
        if (curLen == reqLen) {
            this.args = args;
        } else {
            this.args = Arrays.copyOf(args, reqLen);
            if (curLen < reqLen)
                Arrays.fill(this.args, curLen, reqLen, "");
        }
        this.argTypes = argTypes;
        int[] locatorIndexes = new int[argTypes.length];
        int locCnt = 0;
        for (int i = 0; i < argTypes.length; i++) {
            switch (argTypes[i]) {
            case VALUE:
                // skip
                break;
            case LOCATOR:
            case ATTRIBUTE_LOCATOR:
            case CSS_LOCATOR:
            case OPTION_LOCATOR:
                locatorIndexes[locCnt++] = i;
                break;
            }
        }
        this.locatorIndexes = Arrays.copyOf(locatorIndexes, locCnt);
    }

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

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
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
        for (int locatorIndex : locatorIndexes) {
            ArgumentType type = argTypes[locatorIndex];
            String arg = args[locatorIndex];
            switch (type) {
            case CSS_LOCATOR:
                if (!arg.startsWith("css="))
                    arg = "css=" + arg;
                // fall through
            case LOCATOR:
                locators[i++] = arg;
                break;
            case ATTRIBUTE_LOCATOR:
                int at = arg.lastIndexOf('@');
                locators[i++] = at >= 0 ? arg.substring(0, at) : arg;
                break;
            case OPTION_LOCATOR:
                locators[i] = WebDriverElementFinder.convertToOptionLocatorWithParent(locators[i - 1], arg);
                i++;
                break;
            default:
                // not reached.
                throw new RuntimeException("Invalid locator type: " + type);
            }
        }
        return locators;
    }

    @Override
    public boolean mayUpdateScreen() {
        return true;
    }

    protected abstract Result executeImpl(Context context, String... curArgs);

    @Override
    public final Result execute(Context context, String... curArgs) {
        return result = executeImpl(context, curArgs);
    }

    @Override
    public Result getResult() {
        return result;
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

    static String toString(int index, String name, String[] args) {
        StringBuilder s = new StringBuilder("Command#").append(index);
        s.append(": ").append(name).append("(");
        boolean sep = false;
        for (String arg : args) {
            if (sep)
                s.append(", ");
            else
                sep = true;
            s.append(LoggerUtils.quote(arg));
        }
        s.append(')');
        return s.toString();
    }

    @Override
    public String toString() {
        return toString(index, name, args);
    }
}
