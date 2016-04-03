package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.utils.DialogOverride;

/**
 * Command "IsPromptPresent".
 */
public class IsPromptPresent extends AbstractSubCommand<Boolean> {

    private final DialogOverride dialogOverride;

    /**
     * Constructor.
     *
     * @param dialogOverride dialog override.
     */
    public IsPromptPresent(DialogOverride dialogOverride) {
        super();
        this.dialogOverride = dialogOverride;
    }

    @Override
    public Boolean execute(Context context, String... args) {
        return dialogOverride.isPromptPresent(context.getWrappedDriver());
    }
}
