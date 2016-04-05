package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.utils.DialogOverride;

/**
 * Command "getPrompt".
 */
public class GetPrompt extends AbstractSubCommand<String> {

    private final DialogOverride dialogOverride;

    /**
     * Constructor.
     *
     * @param dialogOverride dialog override.
     */
    public GetPrompt(DialogOverride dialogOverride) {
        super();
        this.dialogOverride = dialogOverride;
    }

    @Override
    public String execute(Context context, String... args) {
        return dialogOverride.getNextPrompt(context.getWrappedDriver());
    }
}
