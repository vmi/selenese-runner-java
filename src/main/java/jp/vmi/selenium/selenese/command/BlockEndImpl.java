package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Default implementation for EndLoop.
 */
public abstract class BlockEndImpl extends AbstractCommand implements BlockEnd {

    BlockEndImpl(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, argTypes);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        BlockStart blockStart = getBlockStart();
        if (blockStart.isLoopBlock()) {
            FlowControlState state = context.getFlowControlState((ICommand) blockStart);
            int index = ((ICommand) blockStart).getIndex();
            if (state.isAlreadyFinished()) {
                context.setFlowControlState((ICommand) blockStart, null);
                return new Success("Exit loop (#" + index + ")");
            } else {
                context.getCommandListIterator().jumpTo(blockStart);
                return new Success("Go to next loop (#" + index + ")");
            }
        } else {
            return SUCCESS;
        }
    }
}
