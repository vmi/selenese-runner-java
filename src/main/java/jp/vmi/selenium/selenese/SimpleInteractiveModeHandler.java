package jp.vmi.selenium.selenese;

import java.util.Arrays;
import java.util.Scanner;

import jp.vmi.selenium.selenese.command.CommandListIterator;
import jp.vmi.selenium.selenese.command.Comment;
import jp.vmi.selenium.selenese.command.CurrentCommand;
import jp.vmi.selenium.selenese.command.Debugger;

/**
 * Simple interactive mode handler.
 */
public class SimpleInteractiveModeHandler implements InteractiveModeHandler {

    private static final Scanner systemInReader = new Scanner(System.in);

    private boolean isEnabled = false;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public void enableIfBreakpointReached(CurrentCommand curCmd) {
        if (curCmd.command instanceof Debugger
            || (curCmd.command instanceof Comment
                && curCmd.curArgs.length > 0
                && curCmd.curArgs[0].trim().equals("breakpoint")))
            setEnabled(true);
    }

    @Override
    public CurrentCommand handle(Context context, CommandListIterator commandListIterator, CurrentCommand curCmd) {
        if (!isEnabled)
            return curCmd;
        boolean doRepeat = true;
        while (doRepeat) {
            System.out.println(">>>>>Interactive mode<<<<<");
            System.out.println("Current command: " + curCmd.command.getName() + " " + Arrays.toString(curCmd.command.getArguments()));
            System.out.println("Input <space> or <return> to run. Input c to exit interactive mode. Input < to previous command. Input > to next command.");
            String userInputKey = systemInReader.nextLine();
            switch (userInputKey) {

            case "": // Enter
            case " ":
                doRepeat = false;
                break;

            case "c":
                setEnabled(false);
                doRepeat = false;
                break;

            case "<":
                commandListIterator.jumpTo(curCmd.command);
                if (commandListIterator.hasPrevious()) {
                    curCmd = new CurrentCommand(context, commandListIterator.previous());
                    commandListIterator.next();
                }
                break;

            case ">":
                if (commandListIterator.hasNext()) {
                    curCmd = new CurrentCommand(context, commandListIterator.next());
                }
                break;
            }
        }
        return curCmd;
    }
}
