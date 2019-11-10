package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;

/**
 * Current command container.
 */
@SuppressWarnings("javadoc")
public class CurrentCommand {
    public final ICommand command;
    public final String[] curArgs;

    public CurrentCommand(Context context, ICommand command) {
        this.command = command;
        curArgs = command.getVariableResolvedArguments(context.getCurrentTestCase().getSourceType(), context.getVarsMap());
    }
}
