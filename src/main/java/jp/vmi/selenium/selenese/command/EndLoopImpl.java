package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for EndLoop.
 *
 * @deprecated use {@link BlockEndImpl} instead.
 */
@Deprecated
public abstract class EndLoopImpl extends BlockEndImpl {

    EndLoopImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }
}
