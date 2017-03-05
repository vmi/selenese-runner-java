package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of DeleteAllVisibleCookies.
 */
public class DeleteAllVisibleCookies extends AbstractCommand {

    DeleteAllVisibleCookies(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getWrappedDriver().manage().deleteAllCookies();
        return SUCCESS;
    }
}
