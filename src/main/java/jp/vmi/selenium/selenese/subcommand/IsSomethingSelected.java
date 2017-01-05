package jp.vmi.selenium.selenese.subcommand;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Alternative IsSomethingSelected.
 */
public class IsSomethingSelected extends AbstractSubCommand<Boolean> {

    private static int ARG_SELECT_LOCATOR = 0;

    /**
     * Constructor.
     */
    public IsSomethingSelected() {
        super(LOCATOR);
    }

    @Override
    public Boolean execute(Context context, String... args) {
        WebElement select = context.findElement(args[ARG_SELECT_LOCATOR]);
        List<WebElement> options = select.findElements(By.tagName("option"));
        for (WebElement option : options)
            if (option.isSelected())
                return true;
        return false;
    }
}
