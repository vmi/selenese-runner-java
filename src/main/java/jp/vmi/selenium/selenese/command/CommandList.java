package jp.vmi.selenium.selenese.command;

/**
 * Command list holder.
 *
 * This holds the first element of Command list.
 *
 * This is pseudo command, not real one.
 */
public class CommandList extends Command {

    /**
     * Constructor.
     */
    public CommandList() {
        super(0, "COMMAND_LIST");
    }

    /**
     * Get first command of command list.
     *
     * @return command.
     */
    public Command first() {
        return next(null);
    }
}
