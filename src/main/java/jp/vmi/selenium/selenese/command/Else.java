package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.If.IfState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

/**
 * Command "else".
 */
public class Else extends BlockStartImpl implements BlockEnd {

    Else(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        IfState state = context.getFlowControlState((ICommand) getBlockStart());
        context.setFlowControlState(this, state);
        if (state.isAlreadyFinished()) {
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("Skip else");
        } else {
            state.setAlreadyFinished(true);
            return new Success("Do else");
        }
    }
}
