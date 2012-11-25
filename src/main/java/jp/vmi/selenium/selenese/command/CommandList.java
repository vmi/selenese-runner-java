package jp.vmi.selenium.selenese.command;

/**
 * Command list holder.
 *
 * This holds the first element of Command list.
 *
 * This is pseudo command, not real one.
 */
public class CommandList extends Command {

    private static final String[] EMPTY_ARGS = new String[0];

    /**
     * Constructor.
     */
    public CommandList() {
        super(0, "COMMAND_LIST", EMPTY_ARGS, 0);
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
