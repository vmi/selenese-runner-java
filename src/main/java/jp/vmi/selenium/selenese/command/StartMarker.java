package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Start marker for result sequence.
 */
public class StartMarker implements ICommand {

    private final ICommand command;

    /**
     * Constructor.
     *
     * @param command real command.
     * @param message message for log.
     */
    public StartMarker(ICommand command, String message) {
        this.command = command;
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
    public Locator[] extractLocators(String[] args) {
        return this.command.extractLocators(args);
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
    public void setStartLoop(StartLoop startLoop) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StartLoop getStartLoop() {
        throw new UnsupportedOperationException();
    }
}
