package jp.vmi.selenium.selenese.subcommand;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.ElementFinder;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

/**
 * Alternative IsSomethingSelected.
 */
public class IsSomethingSelected extends SeleneseCommand<Boolean> {

    private final ElementFinder finder;

    /**
     * Constructor.
     *
     * @param finder element finder.
     */
    public IsSomethingSelected(ElementFinder finder) {
        this.finder = finder;
    }

    @Override
    protected Boolean handleSeleneseCommand(WebDriver driver, String selectLocator, String ignored) {
        WebElement select = finder.findElement(driver, selectLocator);
        List<WebElement> options = select.findElements(By.tagName("option"));
        for (WebElement option : options)
            if (option.isSelected())
                return true;
        return false;
    }
}
