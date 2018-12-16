package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "times".
 */
public class Times extends BlockStartImpl {

    private static final int ARG_TIMES = 0;

    private static class TimesState implements FlowControlState {

        private final long times;
        private long count = 1;

        TimesState(long times) {
            this.times = times;
        }

        void incrementCount() {
            count++;
        }

        long getTimes() {
            return times;
        }

        long getCount() {
            return count;
        }

        @Override
        public boolean isAlreadyFinished() {
            return count > times;
        }
    }

    Times(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean isLoopBlock() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        TimesState state = context.getFlowControlState(this);
        if (state != null) {
            state.incrementCount();
            if (state.isAlreadyFinished()) {
                context.setFlowControlState(this, null);
                context.getCommandListIterator().jumpToNextOf(blockEnd);
                return new Success("Finished " + state.getTimes() + " times repitition");
            }
        } else {
            long times = ((Number) context.executeScript("return (" + curArgs[ARG_TIMES] + ")")).longValue();
            if (times <= 0) {
                context.getCommandListIterator().jumpToNextOf(blockEnd);
                return new Success("Skip: times is " + times);
            }
            state = new TimesState(times);
            context.setFlowControlState(this, state);
        }
        return new Success(String.format("Times: %d/%d", state.getCount(), state.getTimes()));
    }
}
