package jp.vmi.selenium.runner.model.side;

import java.util.List;

import jp.vmi.selenium.runner.model.ITest;

/**
 * "test" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideTest extends SideBase implements ITest<SideCommand> {

    private List<SideCommand> commands = null;

    @Override
    public List<SideCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<SideCommand> commands) {
        this.commands = commands;
    }
}
