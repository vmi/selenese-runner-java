package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Supplier;

/**
 * WebDriver reviver.
 */
public interface WebDriverPreparator extends Supplier<WebDriver> {

    /**
     * Re-prepare WebDriver.
     *
     * Restart WebDriver if it crashed.
     *
     * @param driver WebDriver.
     * @return WebDriver.
     */
    WebDriver reprepare(WebDriver driver);
}
