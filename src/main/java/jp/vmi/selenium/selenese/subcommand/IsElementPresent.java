package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.WebDriverException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of IsElementPresent.
 */
public class IsElementPresent extends AbstractSubCommand<Boolean> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public IsElementPresent() {
        super(ArgumentType.LOCATOR);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        try {
            return context.findElement(args[ARG_LOCATOR]) != null;
        } catch (WebDriverException e) {
            return false;
        }
    }
}
