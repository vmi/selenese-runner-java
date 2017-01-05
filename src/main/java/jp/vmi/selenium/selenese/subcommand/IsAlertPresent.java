package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * "isAlertPresent".
 */
public class IsAlertPresent extends AbstractSubCommand<Boolean> {

    @Override
    public Boolean execute(Context context, String... args) {
        return context.getJSLibrary().isAlertPresent(context.getWrappedDriver());
    }
}
