package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "if".
 */
public class If extends BlockStartImpl {

    private static final int ARG_CONDITION = 0;

    static class IfState implements FlowControlState {

        private boolean isAlreadyFinished;

        IfState(boolean isAlreadyFinished) {
            this.isAlreadyFinished = isAlreadyFinished;
        }

        @Override
        public boolean isAlreadyFinished() {
            return isAlreadyFinished;
        }

        public void setAlreadyFinished(boolean isAlreadyFinished) {
            this.isAlreadyFinished = isAlreadyFinished;
        }
    }

    If(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (context.isTrue(curArgs[ARG_CONDITION])) {
            context.setFlowControlState(this, new IfState(true));
            return new Success("True");
        } else {
            context.setFlowControlState(this, new IfState(false));
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("False");
        }
    }
}
