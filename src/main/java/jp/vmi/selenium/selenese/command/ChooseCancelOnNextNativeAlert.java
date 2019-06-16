package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "chooseCancelOnNextNativeAlert".
 */
public class ChooseCancelOnNextNativeAlert extends AbstractCommand {

    ChooseCancelOnNextNativeAlert(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean isNativeAlertHandler() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getNextNativeAlertActionListener().setAccept(false);
        return SUCCESS;
    }
}
