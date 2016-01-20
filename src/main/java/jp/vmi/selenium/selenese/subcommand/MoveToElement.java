package jp.vmi.selenium.selenese.subcommand;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.thoughtworks.selenium.webdriven.ElementFinder;
import com.thoughtworks.selenium.webdriven.SeleneseCommand;

/**
 * Command "clickAt" which allow no coordinates parameter.
 */
public class MoveToElement extends SeleneseCommand<Void> {

    private final ElementFinder finder;

    /**
     * Constructor.
     *
     * @param elementFinder element finder.
     */
    public MoveToElement(ElementFinder elementFinder) {
        this.finder = elementFinder;
    }

    @Override
    protected Void handleSeleneseCommand(WebDriver driver, String locator, String value) {
        WebElement element = finder.findElement(driver, locator);

        new Actions(driver).moveToElement(element).perform();
        return null;
    }
}
