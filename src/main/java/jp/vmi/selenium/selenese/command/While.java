package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "while".
 */
public class While extends BlockStartImpl {

    private static final int ARG_CONDITION = 0;
    private static final int ARG_LOOP_LIMIT = 1;

    private static final String LOOP_LIMIT_MESSAGE = "Max retry limit exceeded (count=%d/limit=%d). To override it, specify a new limit in the value input field";

    private static class WhileState implements FlowControlState {

        private boolean isAlreadyFinished = false;
        private long count = 0;

        void incrementCount() {
            count++;
        }

        long getCount() {
            return count;
        }

        @Override
        public boolean isAlreadyFinished() {
            return isAlreadyFinished;
        }

        public void setAlreadyFinished(boolean isAlreadyFinished) {
            this.isAlreadyFinished = isAlreadyFinished;
        }

        boolean hasReachedLoopLimit(long loopLimit) {
            if (isAlreadyFinished || loopLimit == Long.MAX_VALUE)
                return false;
            return count > loopLimit;
        }
    }

    While(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean isLoopBlock() {
        return true;
    }

    private static long getLongValue(Context context, String s) {
        return ((Number) context.executeScript("return (" + s + ")")).longValue();
    }

    private static long getLoopLimit(Context context, String[] args, int index) {
        if (index >= args.length)
            return Long.MAX_VALUE;
        String s = args[index];
        if (s == null || s.isEmpty())
            return Long.MAX_VALUE;
        return getLongValue(context, s);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        long loopLimit = getLoopLimit(context, curArgs, ARG_LOOP_LIMIT);
        WhileState state = context.getFlowControlState(this);
        if (state == null) {
            state = new WhileState();
            context.setFlowControlState(this, state);
        }
        state.incrementCount();
        if (state.hasReachedLoopLimit(loopLimit)) {
            return new Error(String.format(LOOP_LIMIT_MESSAGE, state.getCount(), loopLimit));
        } else if (!context.isTrue(curArgs[ARG_CONDITION])) {
            state.setAlreadyFinished(true);
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("Break");
        } else {
            String msg;
            if (loopLimit != Long.MAX_VALUE)
                msg = String.format("Continue (count=%d/limit=%d)", state.getCount(), loopLimit);
            else
                msg = String.format("Continue (count=%d)", state.getCount());
            return new Success(msg);
        }
    }
}
