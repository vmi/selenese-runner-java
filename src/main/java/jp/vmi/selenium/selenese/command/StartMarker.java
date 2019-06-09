package jp.vmi.selenium.selenese.command;

import java.util.Collections;
import java.util.List;

import jp.vmi.selenium.runner.model.side.SideCommand;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

/**
 * Start marker for result sequence.
 */
public class StartMarker implements ICommand {

    private final ICommand command;
    private final Result result;

    /**
     * Constructor.
     *
     * @param command real command.
     * @param message message for log.
     */
    public StartMarker(ICommand command, String message) {
        this.command = command;
        this.result = new Success(message);
    }

    @Override
    public String[] getSource() {
        return this.command.getSource();
    }

    @Override
    public int getIndex() {
        return this.command.getIndex();
    }

    @Override
    public String[] getArguments() {
        return this.command.getArguments();
    }

    @Override
    public String getName() {
        return this.command.getName();
    }

    @Deprecated
    @Override
    public String[] convertLocators(String[] args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean mayUpdateScreen() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result execute(Context context, String... curArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    public void setBlockStart(BlockStart blockStart) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockStart getBlockStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void setStartLoop(StartLoop startLoop) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public StartLoop getStartLoop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addScreenshot(String path, String label) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Screenshot> getScreenshots() {
        return Collections.emptyList();
    }

    @Override
    public void setSideCommand(SideCommand sideCommand) {
        command.setSideCommand(sideCommand);
    }
}
