package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.SUCCESS;

public class ChooseOkOnNextNativeAlert extends AbstractCommand {

    public ChooseOkOnNextNativeAlert(int index, String name, String[] args) {
        super(index, name, args);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getNextNativeAlertAction().setAccept(true);
        return SUCCESS;
    }
}
