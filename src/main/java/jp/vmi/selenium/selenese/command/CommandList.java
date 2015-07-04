package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.inject.DoCommand;
import jp.vmi.selenium.selenese.result.CommandResult;
import jp.vmi.selenium.selenese.result.CommandResultList;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Command list.
 */
public class CommandList extends ArrayList<ICommand> {

    private static final long serialVersionUID = 1L;

    private final Map<Object, Integer> indexCache = new HashMap<Object, Integer>();

    @Override
    public boolean add(ICommand command) {
        if (command instanceof ILabel)
            indexCache.put(((ILabel) command).getLabel(), size());
        return super.add(command);
    }

    /**
     * Get index by label or command.
     *
     * @param key label string or ICommand object.
     * @return index or -1.
     */
    @Override
    public int indexOf(Object key) {
        Integer index = indexCache.get(key);
        if (index == null) {
            index = super.indexOf(key);
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
        return super.listIterator(index);
    }

    /**
     * DO NOT USE THIS METHOD.
     */
    @Deprecated
    @Override
    public CommandListIterator listIterator(int index) {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
    }

    @Deprecated
    @Override
    public CommandListIterator listIterator() {
        throw new UnsupportedOperationException(new Object() {
        }.getClass().getEnclosingMethod().toString());
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
        } catch (Exception e) {
            return new Error(e);
        }
    }

    private static final Pattern JS_BLOCK_RE = Pattern.compile("javascript\\{(.*)\\}", Pattern.DOTALL);

    protected void evalCurArgs(Context context, String[] curArgs) {
        for (int i = 0; i < curArgs.length; i++) {
            Matcher matcher = JS_BLOCK_RE.matcher(curArgs[i]);
            if (matcher.matches()) {
                Object value = context.getEval().eval(context.getWrappedDriver(), matcher.group(1));
                if (value == null)
                    value = "";
                curArgs[i] = value.toString();
            }
        }
    }

    /**
     * Execute command list.
     *
     * @deprecated Use {@link #execute(Context, CommandResultList)}.
     *
     * @param context Selenese Runner context.
     * @return result of command list execution.
     */
    @Deprecated
    public Result execute(Context context) {
        return execute(context, new CommandResultList());
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
        boolean isContinued = true;
        try {
            while (isContinued && commandListIterator.hasNext()) {
                ICommand command = commandListIterator.next();
                sequence.increment(command);
                List<Screenshot> ss = command.getScreenshots();
                int prevSSIndex = (ss == null) ? 0 : ss.size();
                String[] curArgs = context.getVarsMap().replaceVarsForArray(command.getArguments());
                evalCurArgs(context, curArgs);
                Result result = doCommand(context, command, curArgs);
                if (result.isAborted())
                    isContinued = false;
                else
                    context.waitSpeed();
                ss = command.getScreenshots();
                List<Screenshot> newSS;
                if (ss == null || prevSSIndex == ss.size())
                    newSS = null;
                else
                    newSS = new ArrayList<Screenshot>(ss.subList(prevSSIndex, ss.size()));
                CommandResult cresult = new CommandResult(sequence.toString(), command, newSS, result, cresultList.getEndTime(), System.currentTimeMillis());
                cresultList.add(cresult);

            }
        } finally {
            context.popCommandListIterator();
        }
        return cresultList.getResult();
    }
}
