package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
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
        if (getBlockStart().isSkippedNextBlock()) {
            context.getCommandListIterator().jumpToNextOf(blockEnd);
            return new Success("Skip else");
        } else {
            return new Success("Do else");
        }
    }

    @Override
    public boolean isSkippedNextBlock() {
        return true;
    }
}
