package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "chooseOkOnNextNativeAlert".
 */
public class ChooseOkOnNextNativeAlert extends AbstractCommand {

    ChooseOkOnNextNativeAlert(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean isNativeAlertHandler() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getNextNativeAlertActionListener().setAccept(true);
        return SUCCESS;
    }
}
