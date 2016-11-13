package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of GetText.
 */
public class GetText extends AbstractSubCommand<String> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public GetText() {
        super(ArgumentType.LOCATOR);
    }

    @Override
    public String execute(Context context, String... args) {
        return context.findElement(args[ARG_LOCATOR]).getText();
    }
}
