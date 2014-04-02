package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Locator handler.
 */
public interface LocatorHandler {

    /**
     * Locator type.
     *
     * @return locator type.
     */
    String locatorType();

    /**
     * Handle locator.
     *
     * @param driver WebDriver instance.
     * @param arg locator argument.
     *
     * @return found element.
     */
    List<WebElement> handle(WebDriver driver, String arg);
}
