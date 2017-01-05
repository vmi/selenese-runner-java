package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * "getAlert".
 */
public class GetAlert extends AbstractSubCommand<String> {

    @Override
    public String execute(Context context, String... args) {
        return context.getJSLibrary().getNextAlert(context.getWrappedDriver());
    }
}
