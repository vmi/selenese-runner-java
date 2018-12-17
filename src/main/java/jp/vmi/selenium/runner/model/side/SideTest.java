package jp.vmi.selenium.runner.model.side;

import java.util.ArrayList;
import java.util.List;

import jp.vmi.selenium.runner.model.ITest;

/**
 * "test" element of side format.
 */
@SuppressWarnings("javadoc")
public class SideTest extends SideBase implements ITest<SideCommand> {

    private List<SideCommand> commands = null;

    public SideTest(boolean isGen) {
        super(isGen);
    }

    @Override
    public List<SideCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<SideCommand> commands) {
        this.commands = commands;
    }

    public void addCommand(SideCommand command) {
        if (commands == null)
            commands = new ArrayList<>();
        commands.add(command);
    }
}
