package jp.vmi.selenium.selenese.side;

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
