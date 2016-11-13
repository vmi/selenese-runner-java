package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of IsChecked.
 */
public class IsChecked extends AbstractSubCommand<Boolean> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public IsChecked() {
        super(ArgumentType.LOCATOR);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        return context.findElement(args[ARG_LOCATOR]).isSelected();
    }
}
