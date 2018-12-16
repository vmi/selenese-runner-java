package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.If.IfState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "if".
 */
public class ElseIf extends BlockStartImpl implements BlockEnd {

    private static final int ARG_CONDITION = 0;

    ElseIf(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        IfState state = context.getFlowControlState((ICommand) getBlockStart());
        context.setFlowControlState(this, state);
        if (state.isAlreadyFinished()) {
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("Skip else if");
        } else if (context.isTrue(curArgs[ARG_CONDITION])) {
            state.setAlreadyFinished(true);
            return new Success("True");
        } else {
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("False");
        }
    }
}
