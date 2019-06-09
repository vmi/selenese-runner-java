package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import jp.vmi.selenium.runner.model.ICommandSignature;
import jp.vmi.selenium.runner.model.side.SideCommand;
import jp.vmi.selenium.runner.model.side.SideCommandMetadata;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.VarsMap;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

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
    private final ICommandSignature commandSignature;
    private Result result = UNEXECUTED;
    private BlockStart blockStart = BlockStart.NO_BLOCK_START;
    private List<Screenshot> screenshots = null;
    private SideCommand sideCommand = null;

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
        this.commandSignature = SideCommandMetadata.getInstance().getSignature(name);
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
    public String[] getVariableResolvedArguments(SourceType sourceType, VarsMap varsMap) {
        if (sourceType == SourceType.SIDE && commandSignature != null) {
            String[] curArgs = new String[args.length];
            switch (args.length) {
            case 2:
                curArgs[1] = varsMap.replaceVars(commandSignature.isValueScript(), args[1]);
                // fall through.
            case 1:
                curArgs[0] = varsMap.replaceVars(commandSignature.isTargetScript(), args[0]);
                // fall through.
            case 0:
                break;
            default:
                throw new SeleneseRunnerRuntimeException("Don't support 3 or more arguments.");
            }
            return curArgs;
        } else {
            return Arrays.stream(getArguments()).map(arg -> varsMap.replaceVars(false, arg)).toArray(String[]::new);
        }
    }

    @Override
    @Deprecated
    public String[] convertLocators(String[] args) {
        return Arrays.stream(extractLocators(args)).map(Locator::toLocatorString).toArray(String[]::new);
    }

    @Override
    public Locator[] extractLocators(String[] args) {
        if (locatorIndexes.length == 0)
            return Locator.EMPTY_ARRAY;
        Locator[] plocs = new Locator[locatorIndexes.length];
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
                plocs[i++] = new Locator(arg);
                break;
            case ATTRIBUTE_LOCATOR:
                int at = arg.lastIndexOf('@');
                plocs[i++] = new Locator(at >= 0 ? arg.substring(0, at) : arg);
                break;
            case OPTION_LOCATOR:
                plocs[i] = plocs[i - 1].withOption(arg);
                i++;
                break;
            default:
                // not reached.
                throw new RuntimeException("Invalid locator type: " + type);
            }
        }
        return plocs;
    }

    @Override
    public boolean mayUpdateScreen() {
        return true;
    }

    protected abstract Result executeImpl(Context context, String... curArgs);

    @Override
    public final Result execute(Context context, String... curArgs) {
        try {
            result = executeImpl(context, curArgs);
        } catch (TimeoutException e) {
            result = new Error("Timed out");
        } catch (WebDriverException e) {
            result = new Failure(e.getMessage().replaceAll("(\r?\n)+", " / "));
        }
        return result;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void setBlockStart(BlockStart blockStart) {
        this.blockStart = blockStart;
    }

    @Override
    public BlockStart getBlockStart() {
        return blockStart;
    }

    @Override
    @SuppressWarnings("deprecation")
    final public void setStartLoop(StartLoop startLoop) {
        setBlockStart(startLoop);
    }

    @Override
    @SuppressWarnings("deprecation")
    final public StartLoop getStartLoop() {
        BlockStart blockStart = getBlockStart();
        if (blockStart instanceof StartLoop)
            return (StartLoop) blockStart;
        else
            return new StartLoop() {

                @Override
                public void setBlockEnd(BlockEnd blockEnd) {
                    blockStart.setBlockEnd(blockEnd);
                }

                @Override
                public void setEndLoop(EndLoop endLoop) {
                    blockStart.setBlockEnd(endLoop);
                }
            };
    }

    @Override
    public void addScreenshot(String path, String label) {
        if (path == null)
            return;
        if (screenshots == null)
            screenshots = new ArrayList<>();
        screenshots.add(new Screenshot(path, label));
    }

    @Override
    public List<Screenshot> getScreenshots() {
        if (screenshots == null)
            return Collections.emptyList();
        return screenshots;
    }

    @Override
    public void setSideCommand(SideCommand sideCommand) {
        this.sideCommand = sideCommand;
    }

    SideCommand getSideCommand() {
        return sideCommand;
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
            LoggerUtils.quote(s, arg);
        }
        s.append(')');
        return s.toString();
    }

    @Override
    public String toString() {
        return toString(index, name, args);
    }
}
