package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for StartLoop.
 */
public abstract class StartLoopImpl extends AbstractCommand implements StartLoop {

    StartLoopImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }
}
