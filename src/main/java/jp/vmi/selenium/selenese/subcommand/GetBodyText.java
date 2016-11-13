package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.By;

import jp.vmi.selenium.selenese.Context;

/**
 * Re-implementation of GetBodyText.
 */
public class GetBodyText extends AbstractSubCommand<String> {

    /**
     * Constructor.
     */
    public GetBodyText() {
        super();
    }

    @Override
    public String execute(Context context, String... args) {
        return context.getWrappedDriver().findElement(By.tagName("body")).getText();
    }
}
