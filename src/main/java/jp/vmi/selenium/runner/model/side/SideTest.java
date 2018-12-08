package jp.vmi.selenium.runner.model.side;

import java.util.List;

/**
 * "test" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideTest extends SideBase {

    private List<SideCommand> commands;

    public List<SideCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<SideCommand> commands) {
        this.commands = commands;
    }
}
