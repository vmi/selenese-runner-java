package jp.vmi.selenium.selenese.command;

import java.util.ArrayList;

/**
 * Command list.
 */
public class CommandList extends ArrayList<Command> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(Command command) {
        if (!isEmpty())
            get(size() - 1).setNext(command);
        return super.add(command);
    }

    /**
     * Get first command of command list.
     *
     * @return command.
     */
    public Command first() {
        return isEmpty() ? null : get(0);
    }
}
