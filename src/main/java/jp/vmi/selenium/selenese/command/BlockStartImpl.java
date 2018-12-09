package jp.vmi.selenium.selenese.command;

/**
 * Default implementation for BlockStart.
 */
public abstract class BlockStartImpl extends AbstractCommand implements BlockStart {

    protected BlockEnd blockEnd;

    BlockStartImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    public void setBlockEnd(BlockEnd blockEnd) {
        this.blockEnd = blockEnd;
    }
}
