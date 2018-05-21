package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "chooseCancelOnNextPrompt".
 */
public class ChooseCancelOnNextPrompt extends AbstractCommand {

    ChooseCancelOnNextPrompt(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getJSLibrary().answerOnNextPrompt(context.getWrappedDriver(), null);
        return SUCCESS;
    }
}
