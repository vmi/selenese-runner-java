package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * Command "IsPromptPresent".
 */
public class IsPromptPresent extends AbstractSubCommand<Boolean> {

    @Override
    public Boolean execute(Context context, String... args) {
        return context.getJSLibrary().isPromptPresent(context.getWrappedDriver());
    }
}
