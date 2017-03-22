package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Created by fima on 22.03.17.
 */
public class GetAttributeCSS extends AbstractSubCommand<String> {

    private static final int ARG_ATTRIBUTE_LOCATOR = 0;

    /**
     * Constructor.
     */
    public GetAttributeCSS() {
        super(ArgumentType.ATTRIBUTE_LOCATOR);
    }

    @Override
    public String execute(Context context, String... args) {
        String attrLocator = args[ARG_ATTRIBUTE_LOCATOR];
        int index = attrLocator.lastIndexOf('@');
        String locator = attrLocator.substring(0, index);
        String attrName = attrLocator.substring(index + 1);
        String cssValue = context.findElement(locator).getCssValue(attrName);
        String newCssValue = null;
        if (cssValue.contains("rgba")) {
            newCssValue = cssValue.replace("rgba", "rgb").replace(", 1", "");
        }
        return newCssValue;
    }
}
