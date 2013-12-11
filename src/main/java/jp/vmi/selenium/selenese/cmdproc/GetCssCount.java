package jp.vmi.selenium.selenese.cmdproc;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 *  
 */
public class GetCssCount extends SeleneseCommand<Number> {

    private static final String CSS_EQ = "css=";

    private final WebDriverElementFinder finder;

    /**
     * Constructor.
     *
     * @param finder element finder.
     */
    public GetCssCount(WebDriverElementFinder finder) {
        this.finder = finder;
    }

    @Override
    protected Number handleSeleneseCommand(WebDriver driver, String cssLocator, String ignored) {
        if (!cssLocator.startsWith(CSS_EQ))
            cssLocator = CSS_EQ + cssLocator;
        List<WebElement> elements = finder.findElements(driver, cssLocator);
        return elements.size();
    }
}
