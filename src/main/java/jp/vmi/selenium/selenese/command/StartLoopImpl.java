package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for StartLoop.
 */
@Deprecated
public abstract class StartLoopImpl extends BlockStartImpl {

    StartLoopImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }
}
