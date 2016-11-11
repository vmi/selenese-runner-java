package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * "isConfirmationPresent".
 */
public class IsConfirmationPresent extends AbstractSubCommand<Boolean> {

    @Override
    public Boolean execute(Context context, String... args) {
        return context.getJSLibrary().isConfirmationPresent(context.getWrappedDriver());
    }
}
