package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Re-implementation of IsEditable.
 */
public class IsEditable extends AbstractSubCommand<Boolean> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public IsEditable() {
        super(LOCATOR);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        WebElement element = context.findElement(args[ARG_LOCATOR]);
        if (!element.isEnabled())
            return false;
        String readonly = element.getAttribute("readonly");
        return readonly == null || "false".equals(readonly);
    }
}
