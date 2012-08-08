package jp.vmi.selenium.selenese.command;

/**
 * Command list holder.
 *
 * This holds the first element of Command list.
 *
 * This is pseudo command, not real one.
 */
public class CommandList extends Command {

    public CommandList() {
        super(0, "COMMAND_LIST");
    }

    public Command first() {
        return next(null);
    }
}
