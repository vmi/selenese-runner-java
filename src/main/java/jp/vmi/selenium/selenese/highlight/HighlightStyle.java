package jp.vmi.selenium.selenese.highlight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Highlight style.
 */
public class HighlightStyle {

    /**
     * element styles.
     */
    public static HighlightStyle[] ELEMENT_STYLES = new HighlightStyle[] {
        new HighlightStyle("backgroundColor: yellow", "outline: #8f8 solid 1px"),
        new HighlightStyle("backgroundColor: orange", "outline: #484 solid 1px")
    };

    private static final String SCRIPT = "return (function(element, hlStyle) {\n"
        + "  var backup = {};\n"
        + "  var style = element.style;\n"
        + "  for (var key in hlStyle) {\n"
        + "    backup[key] = style[key];\n"
        + "    style[key] = hlStyle[key];\n"
        + "  }\n"
        + "  return backup;\n"
        + "}).apply(window, arguments);";

    private final Map<String, String> styles;

    /**
     * Constructor.
     *
     * @param styles style for highlighting.
     */
    public HighlightStyle(Map<String, String> styles) {
        this.styles = styles;
    }

    /**
     * Constructor.
     *
     * @param styles style for highlighting.
     */
    public HighlightStyle(String... styles) {
        this.styles = new HashMap<>();
        for (String style : styles) {
            String[] kv = style.split("\\s*:\\s*", 2);
            this.styles.put(kv[0], kv[1]);
        }
    }

    /**
     * Do highlight specified element.
     *
     * @param driver instance of WebDriver.
     * @param elementFinder element finder.
     * @param locator locator to target element.
     * @param selectedFrameLocators selected frame locators.
     * @return previous style.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> doHighlight(WebDriver driver, WebDriverElementFinder elementFinder, String locator, List<Locator> selectedFrameLocators) {
        try {
            WebElement element = elementFinder.findElement(driver, locator, selectedFrameLocators);
            Object result = ((JavascriptExecutor) driver).executeScript(SCRIPT, element, styles);
            return result instanceof Map ? (Map<String, String>) result : null;
        } catch (RuntimeException e) {
            // element specified by locator is not found.
            if (e instanceof NotFoundException || e.getCause() instanceof NotFoundException || e instanceof StaleElementReferenceException)
                return null;
            throw e;
        }
    }
}
