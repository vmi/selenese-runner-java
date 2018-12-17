package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.FlowControlState;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "do".
 */
public class Do extends BlockStartImpl {

    Do(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean isLoopBlock() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.setFlowControlState(this, FlowControlState.CONTINUED_STATE);
        return SUCCESS;
    }
}
