package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.InteractiveModeHandler;
import jp.vmi.selenium.selenese.SeleneseCommandErrorException;
import jp.vmi.selenium.selenese.inject.DoCommand;
import jp.vmi.selenium.selenese.result.CommandResult;
import jp.vmi.selenium.selenese.result.CommandResultList;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Command list.
 */
public class CommandList implements Iterable<ICommand> {

    private final Map<Object, Integer> indexCache = new HashMap<>();
    private final List<ICommand> commandList = new ArrayList<>();

    /**
     * Returns {@code true} if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements.
     */
    public boolean isEmpty() {
        return commandList.isEmpty();
    }

    /**
     * Returns the number of elements in this list.  If this list contains
     * more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this list.
     */
    public int size() {
        return commandList.size();
    }

    /**
     * Add command.
     *
     * @param command command.
     * @return true if this collection changed as a result of the call. (for keeping compatibility)
     */
    public boolean add(ICommand command) {
        if (command instanceof ILabel)
            indexCache.put(((ILabel) command).getLabel(), commandList.size());
        return commandList.add(command);
    }

    /**
     * Get index by label or command.
     *
     * @param key label string or ICommand object.
     * @return index or -1.
     */
    public int indexOf(Object key) {
        Integer index = indexCache.get(key);
        if (index == null) {
            index = commandList.indexOf(key);
            indexCache.put(key, index);
        }
        return index;
    }

    /**
     * Original list iterator.
     *
     * see {@link ArrayList#listIterator(int)}
     *
     * @param index start index.
     * @return ListIterator.
     */
    protected ListIterator<ICommand> originalListIterator(int index) {
        return commandList.listIterator(index);
    }

    @Override
    public CommandListIterator iterator() {
        return iterator(null);
    }

    /**
     * Create the iterator of this command list.
     *
     * @param parentIterator parent iterator.
     *
     * @return iterator.
     */
    public CommandListIterator iterator(CommandListIterator parentIterator) {
        return new CommandListIterator(this, parentIterator);
    }

    @DoCommand
    protected Result doCommand(Context context, ICommand command, String... curArgs) {
        try {
            return command.execute(context, curArgs);
        } catch (SeleneseCommandErrorException e) {
            return e.getError();
        } catch (Exception e) {
            return new Error(e);
        }
    }

    private static final Pattern JS_BLOCK_RE = Pattern.compile("javascript\\{(.*)\\}", Pattern.DOTALL);

    protected void evalCurArgs(Context context, String[] curArgs) {
        for (int i = 0; i < curArgs.length; i++) {
            Matcher matcher = JS_BLOCK_RE.matcher(curArgs[i]);
            if (matcher.matches()) {
                Object value = context.getEval().eval(context, matcher.group(1));
                if (value == null)
                    value = "";
                curArgs[i] = value.toString();
            }
        }
    }

    /**
     * Execute command list.
     *
     * @param context Selenese Runner context.
     * @param cresultList command result list for keeping all command results.
     * @return result of command list execution.
     */
    public Result execute(Context context, CommandResultList cresultList) {
        CommandListIterator parentIterator = context.getCommandListIterator();
        CommandListIterator commandListIterator = iterator(parentIterator);
        context.pushCommandListIterator(commandListIterator);
        CommandSequence sequence = commandListIterator.getCommandSequence();
        InteractiveModeHandler interactiveModeHandler = context.getInteractiveModeHandler();
        boolean isContinued = true;
        try {
            while (isContinued && commandListIterator.hasNext()) {
                CurrentCommand curCmd = new CurrentCommand(context, commandListIterator.next());
                sequence.increment(curCmd.command);
                List<Screenshot> ss = curCmd.command.getScreenshots();
                int prevSSIndex = (ss == null) ? 0 : ss.size();
                evalCurArgs(context, curCmd.curArgs);
                Result result = null;
                context.resetRetries();
                while (true) {
                    interactiveModeHandler.enableIfBreakpointReached(curCmd);
                    curCmd = interactiveModeHandler.handle(context, commandListIterator, curCmd);
                    result = doCommand(context, curCmd.command, curCmd.curArgs);
                    if (result.isSuccess() || context.hasReachedMaxRetries())
                        break;
                    context.incrementRetries();
                    context.waitSpeed();
                }
                if (result.isAborted())
                    isContinued = false;
                else
                    context.waitSpeed();
                ss = curCmd.command.getScreenshots();
                List<Screenshot> newSS;
                if (ss == null || prevSSIndex == ss.size())
                    newSS = null;
                else
                    newSS = new ArrayList<>(ss.subList(prevSSIndex, ss.size()));
                CommandResult cresult = new CommandResult(sequence.toString(), curCmd.command, newSS, result, cresultList.getEndTime(), System.currentTimeMillis());
                cresultList.add(cresult);

            }
        } finally {
            context.popCommandListIterator();
        }
        return cresultList.getResult();
    }

    @Override
    public String toString() {
        return commandList.toString();
    }
}
