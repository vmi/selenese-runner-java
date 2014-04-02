package jp.vmi.selenium.selenese.locator;

import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * Option locator handler.
 */
public interface OptionLocatorHandler {

    /**
     * Option locator type.
     *
     * @return option locator type.
     */
    String optionLocatorType();

    /**
     * Handle option locator.
     * @param element WebElement object.
     * @param arg option locator argument.
     * @return filterd element.
     */
    List<WebElement> handle(WebElement element, String arg);
}
