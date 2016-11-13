package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of IsVisible.
 */
public class IsVisible extends AbstractSubCommand<Boolean> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public IsVisible() {
        super(ArgumentType.LOCATOR);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        return context.findElement(args[ARG_LOCATOR]).isDisplayed();
    }
}
