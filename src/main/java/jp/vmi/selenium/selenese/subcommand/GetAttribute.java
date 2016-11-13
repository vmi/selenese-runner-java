package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of GetAttribute.
 */
public class GetAttribute extends AbstractSubCommand<String> {

    private static final int ARG_ATTRIBUTE_LOCATOR = 0;

    /**
     * Constructor.
     */
    public GetAttribute() {
        super(ArgumentType.ATTRIBUTE_LOCATOR);
    }

    @Override
    public String execute(Context context, String... args) {
        String attrLocator = args[ARG_ATTRIBUTE_LOCATOR];
        int index = attrLocator.lastIndexOf('@');
        String locator = attrLocator.substring(0, index);
        String attrName = attrLocator.substring(index + 1);
        return context.findElement(locator).getAttribute(attrName);
    }
}
