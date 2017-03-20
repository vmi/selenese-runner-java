package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.LOCATOR;
import static jp.vmi.selenium.selenese.command.ArgumentType.OPTION_LOCATOR;
import static jp.vmi.selenium.selenese.result.Success.SUCCESS;

/**
 * Created by fima on 20.03.17.
 */

public class BackgroundColor extends AbstractCommand {
        private static final int ARG_SELECT_LOCATOR = 0;
        private static final int ARG_OPTION_LOCATOR = 1;

    BackgroundColor(int index, String name, String... args) {
        super(index, name, args, LOCATOR, OPTION_LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        GetElementBackgroundColor select = new GetElementBackgroundColor(context, curArgs[ARG_SELECT_LOCATOR]);
        return SUCCESS;
    }
}
