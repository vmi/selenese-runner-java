package jp.vmi.selenium.selenese.locator;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Additional locator handler.
 */
public class AdditionalHandler implements LocatorHandler {

    private static final Logger log = LoggerFactory.getLogger(AdditionalHandler.class);

    private final String locatorType;
    private final String script;

    /**
     * Constructor.
     *
     * @param locatorType locator type name.
     * @param script locator implementation by JavaScript.
     */
    public AdditionalHandler(String locatorType, String script) {
        this.locatorType = locatorType;
        this.script = script;
    }

    @Override
    public String locatorType() {
        return locatorType;
    }

    @Override
    public List<WebElement> handle(WebDriver driver, String arg) {
        List<WebElement> result = new ArrayList<>();
        if (driver instanceof JavascriptExecutor) {
            Object element = ((JavascriptExecutor) driver).executeScript(script, arg);
            if (element instanceof WebElement)
                result.add((WebElement) element);
        } else {
            log.warn("The current WebDriver does not support JavaScript execution.");
        }
        return result;
    }
}
