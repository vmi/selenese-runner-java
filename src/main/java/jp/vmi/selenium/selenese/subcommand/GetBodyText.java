package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
        WebDriver driver = context.getWrappedDriver();
        WebElement body = driver.findElement(By.tagName("body"));
        return context.getJSLibrary().getText(driver, body);
    }
}
