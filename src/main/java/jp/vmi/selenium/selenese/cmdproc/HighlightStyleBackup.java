package jp.vmi.selenium.selenese.cmdproc;

import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Backup and restore style of highlighted element.
 */
public class HighlightStyleBackup extends HighlightStyle {

    private final WebElement element;

    /**
     * Constructor.
     *
     * @param styles style of highlighted element.
     * @param element highlighed element.
     */
    public HighlightStyleBackup(Map<String, String> styles, WebElement element) {
        super(styles);
        this.element = element;
    }

    /**
     * Restore style.
     *
     * @param driver instance of WebDriver.
     */
    public void restore(WebDriver driver) {
        doHighlight(driver, element);
    }
}
