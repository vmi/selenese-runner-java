package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Created by fima on 24.03.17.
 */
public class GetStateOfTab extends AbstractSubCommand {

    private static final int ARG_ATTRIBUTE_LOCATOR = 0;

    public GetStateOfTab() {
        super(ArgumentType.ATTRIBUTE_LOCATOR);
    }

    @Override
    public String execute(Context context, String... args) {
        String locator = args[ARG_ATTRIBUTE_LOCATOR];
        String attrName = "display";
        return context.findElement(locator).getCssValue(attrName);
    }
}
