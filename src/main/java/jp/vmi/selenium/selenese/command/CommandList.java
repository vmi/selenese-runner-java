package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;
import java.util.HashMap;
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
public class CommandList extends ArrayList<ICommand> implements LogIndentLevelHolder {

    private static final long serialVersionUID = 1L;

    private final Map<Object, Integer> indexCache = new HashMap<Object, Integer>();
    private int logIndentLevel = 0;

    @Override
    public int getLogIndentLevel() {
        return logIndentLevel;
    }

    @Override
    public void setLogIndentLevel(int logIndentLevel) {
        this.logIndentLevel = logIndentLevel;
    }

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
            if (index == null)
                return -1;
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
    public ListIterator<ICommand> originalListIterator(int index) {
        return super.listIterator(index);
    }

    @Override
    public CommandListIterator listIterator(int index) {
        return new CommandListIterator(this);
    }

    @Override
    public CommandListIterator listIterator() {
        return listIterator(0);
    }

    @Override
    public CommandListIterator iterator() {
        return listIterator();
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
        CommandListIterator commandListIterator = listIterator();
        context.pushCommandListIterator(commandListIterator);
        boolean isContinued = true;
        try {
            while (isContinued && commandListIterator.hasNext()) {
                ICommand command = commandListIterator.next();
                String[] curArgs = context.getVarsMap().replaceVarsForArray(command.getArguments());
                evalCurArgs(context, curArgs);
                Result result = doCommand(context, command, curArgs);
                if (result.isAborted())
                    isContinued = false;
                else
                    context.waitSpeed();
                CommandResult cresult = new CommandResult(command, result, cresultList.getEndTime(), System.currentTimeMillis());
                cresultList.add(cresult);

            }
        } finally {
            context.popCommandListIterator();
        }
        return cresultList.getResult();
    }
}
