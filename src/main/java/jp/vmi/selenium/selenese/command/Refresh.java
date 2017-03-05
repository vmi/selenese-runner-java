package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Re-implementation of Refresh.
 */
public class Refresh extends AbstractCommand {

    Refresh(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getWrappedDriver().navigate().refresh();
        return SUCCESS;
    }
}
