package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "times".
 */
public class Times extends BlockStartImpl {

    private static final int ARG_TIMES = 0;
    private static final int ARG_LOOP_LIMIT = 1;

    private static final String LOOP_LIMIT_MESSAGE = "Max retry limit exceeded (count=%d/limit=%d). To override it, specify a new limit in the value input field";

    private static class TimesState implements FlowControlState {

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

        boolean hasReachedTimes(long times) {
            if (!isAlreadyFinished && count > times)
                isAlreadyFinished = true;
            return isAlreadyFinished;
        }

        boolean hasReachedLoopLimit(long loopLimit) {
            if (isAlreadyFinished || loopLimit == Long.MAX_VALUE)
                return false;
            return count > loopLimit;
        }
    }

    Times(int index, String name, String... args) {
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
        long times = getLongValue(context, curArgs[ARG_TIMES]);
        long loopLimit = getLoopLimit(context, curArgs, ARG_LOOP_LIMIT);
        TimesState state = context.getFlowControlState(this);
        if (state == null || state.isAlreadyFinished) {
            state = new TimesState();
            context.setFlowControlState(this, state);
        }
        state.incrementCount();
        // Selenium IDE the decision of loop limit first.
        if (state.hasReachedLoopLimit(loopLimit)) {
            return new Error(String.format(LOOP_LIMIT_MESSAGE, state.getCount(), loopLimit));
        } else if (state.hasReachedTimes(times)) {
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("Finished " + times + " times repitition");
        }

        String msg;
        if (loopLimit != Long.MAX_VALUE)
            msg = String.format("Times (count=%d/times=%d/limit=%d)", state.getCount(), times, loopLimit);
        else
            msg = String.format("Times (count=%d/times=%d)", state.getCount(), times);
        return new Success(msg);
    }
}
