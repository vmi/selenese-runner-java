package jp.vmi.selenium.selenese.command;

import java.util.Iterator;
import java.util.List;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.VarsMap;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "forEach".
 */
public class ForEach extends BlockStartImpl {

    private static final int ARG_ARRAY_VAR = 0;
    private static final int ARG_ITER_VAR = 1;

    private static class ForEachState implements FlowControlState, Iterator<Object> {

        private int count;
        private final int maxCount;
        private final Iterator<Object> iter;

        ForEachState(List<Object> array) {
            this.count = 0;
            this.maxCount = array.size();
            this.iter = array.iterator();
        }

        @Override
        public boolean isAlreadyFinished() {
            return !hasNext();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Object next() {
            count++;
            return iter.next();
        }

        int getCount() {
            return count;
        }

        int getMaxCount() {
            return maxCount;
        }
    }

    ForEach(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    public boolean isLoopBlock() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        VarsMap varsMap = context.getVarsMap();
        ForEachState state = context.getFlowControlState(this);
        if (state == null) {
            String arrayVar = curArgs[ARG_ARRAY_VAR];
            Object value = varsMap.get(arrayVar);
            if (value == null || !(value instanceof List))
                return new Error(String.format("%s is not array: %s", arrayVar, value));
            @SuppressWarnings("unchecked")
            List<Object> array = (List<Object>) value;
            state = new ForEachState(array);
            context.setFlowControlState(this, state);
        }
        String iterVar = curArgs[ARG_ITER_VAR];
        if (state.hasNext()) {
            Object value = state.next();
            varsMap.put(iterVar, value);
            int count = state.getCount();
            int maxCount = state.getMaxCount();
            return new Success(String.format("Continue (%d/%d: %s=%s)", count, maxCount, iterVar, value));
        } else {
            context.getCommandListIterator().jumpTo(blockEnd);
            return new Success("Break");
        }
    }
}
