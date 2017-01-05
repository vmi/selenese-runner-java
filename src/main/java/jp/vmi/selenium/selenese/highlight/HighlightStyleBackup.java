package jp.vmi.selenium.selenese.highlight;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Backup and restore style of highlighted element.
 */
public class HighlightStyleBackup extends HighlightStyle {

    private final Locator ploc;
    private final List<Locator> selectedFrameLocator;

    /**
     * Constructor.
     *
     * @param styles style of highlighted element.
     * @param ploc parsed locator to highlighed element.
     * @param selectedFrameLocator selected frame locators.
     */
    public HighlightStyleBackup(Map<String, String> styles, Locator ploc, List<Locator> selectedFrameLocator) {
        super(styles);
        this.ploc = ploc;
        this.selectedFrameLocator = selectedFrameLocator;
    }

    /**
     * Restore style.
     *
     * @param driver instance of WebDriver.
     * @param elementFinder element finder.
     */
    public void restore(WebDriver driver, WebDriverElementFinder elementFinder) {
        doHighlight(driver, elementFinder, ploc, selectedFrameLocator);
    }
}
