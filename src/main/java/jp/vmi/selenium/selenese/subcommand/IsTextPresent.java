package jp.vmi.selenium.selenese.subcommand;

import java.util.List;

import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;
import jp.vmi.selenium.selenese.utils.XPathUtils;

/**
 * "isTextPresent".
 */
public class IsTextPresent extends AbstractSubCommand<Boolean> {

    private static final int ARG_TEXT = 0;

    /**
     * Constructor.
     */
    public IsTextPresent() {
        super(ArgumentType.VALUE);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        StringBuilder xpath = new StringBuilder("//*[contains(normalize-space(.),");
        XPathUtils.appendStringLiteral(xpath, args[ARG_TEXT]);
        xpath.append(")]");
        List<WebElement> elements = context.findElements(xpath.toString());
        return elements != null && !elements.isEmpty();
    }
}
