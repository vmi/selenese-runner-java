package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * "getConfirmation".
 */
public class GetConfirmation extends AbstractSubCommand<String> {

    @Override
    public String execute(Context context, String... args) {
        return context.getJSLibrary().getNextConfirmation(context.getWrappedDriver());
    }
}
