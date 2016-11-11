package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;

/**
 * Command "getPrompt".
 */
public class GetPrompt extends AbstractSubCommand<String> {

    @Override
    public String execute(Context context, String... args) {
        return context.getJSLibrary().getNextPrompt(context.getWrappedDriver());
    }
}
