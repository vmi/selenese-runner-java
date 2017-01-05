package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of IsCookiePresent.
 */
public class IsCookiePresent extends AbstractSubCommand<Boolean> {

    private static final int ARG_COOKIE_NAME = 0;

    /**
     * Constructor.
     */
    public IsCookiePresent() {
        super(ArgumentType.VALUE);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        return context.getWrappedDriver().manage().getCookieNamed(args[ARG_COOKIE_NAME]) != null;
    }
}
