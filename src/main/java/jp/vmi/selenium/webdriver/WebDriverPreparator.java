package jp.vmi.selenium.webdriver;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Supplier;

/**
 * WebDriver reviver.
 */
public interface WebDriverPreparator extends Supplier<WebDriver> {

    /**
     * Get browser name.
     *
     * @return browser name.
     */
    String getBrowserName();
}
