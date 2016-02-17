package jp.vmi.selenium.selenese.subcommand;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * An implementation of the "getSelected(Labels?|Values?|Index(es)?|Ids?)".
 */
public class GetSelected extends AbstractSubCommand<Object> {

    private static final int ARG_LOCATOR = 0;

    @SuppressWarnings("javadoc")
    public static enum Type {
        LABEL, VALUE, INDEX, ID;

        private String getSubCommandName(boolean isMultiple) {
            String prefix = StringUtils.uncapitalize(GetSelected.class.getSimpleName());
            String suffix = StringUtils.capitalize(name().toLowerCase());
            String plural;
            if (isMultiple) {
                switch (this) {
                case INDEX:
                    plural = "es";
                    break;
                default:
                    plural = "s";
                    break;
                }
            } else {
                plural = "";
            }
            return prefix + suffix + plural;
        }
    };

    private final String name;
    private final Type type;
    private final boolean isMultiple;

    /**
     * Constructor.
     *
     * @param type type.
     * @param isMultiple multiple selected.
     */
    public GetSelected(Type type, boolean isMultiple) {
        super(LOCATOR);
        this.name = type.getSubCommandName(isMultiple);
        this.type = type;
        this.isMultiple = isMultiple;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object execute(Context context, String... args) {
        String locator = args[ARG_LOCATOR];
        WebElement select = context.getElementFinder().findElement(context.getWrappedDriver(), locator);
        if (select == null)
            return null;
        List<WebElement> options = select.findElements(By.tagName("option"));
        List<Object> found = new ArrayList<>();
        int i = -1;
        for (WebElement option : options) {
            i++;
            if (option.isSelected()) {
                switch (type) {
                case LABEL:
                    found.add(option.getText());
                    break;
                case VALUE:
                    found.add(option.getAttribute("value"));
                    break;
                case INDEX:
                    found.add(i);
                    break;
                case ID:
                    found.add(option.getAttribute("id"));
                    break;
                default:
                    throw new UnsupportedOperationException(type + " is not implemented.");
                }
            }
        }
        if (isMultiple)
            return found;
        else
            return found.isEmpty() ? null : found.get(0);
    }
}
