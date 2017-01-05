package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Re-implementation of GetSelectOptions.
 */
public class GetSelectOptions extends AbstractSubCommand<String[]> {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     */
    public GetSelectOptions() {
        super(ArgumentType.LOCATOR);
    }

    @Override
    public String[] execute(Context context, String... args) {
        WebElement select = context.findElement(args[ARG_LOCATOR]);
        return select.findElements(By.tagName("option")).stream().map(WebElement::getText).toArray(String[]::new);
    }
}
