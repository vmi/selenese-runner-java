package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "while".
 */
public class While extends BlockStartImpl {

    private static final int ARG_CONDITION = 0;

    private static class WhileState implements FlowControlState {

        private boolean isAlreadyFinished = false;

        @Override
        public boolean isAlreadyFinished() {
            return isAlreadyFinished;
        }

        public void setAlreadyFinished(boolean isAlreadyFinished) {
            this.isAlreadyFinished = isAlreadyFinished;
        }
    }

    While(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean isLoopBlock() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        WhileState state = context.getFlowControlState(this);
        if (state == null) {
            state = new WhileState();
            context.setFlowControlState(this, state);
        }
        if (!context.isTrue(curArgs[ARG_CONDITION])) {
            state.setAlreadyFinished(true);
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("Break");
        } else {
            return new Success("Continue");
        }
    }
}
