package jp.vmi.selenium.selenese.command;

import java.util.function.Function;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Handle click operation.
 */
final class ClickHandler {

    private ClickHandler() {
        // no operation.
    }

    static Result handleClick(Context context, String locator, Function<WebElement, Result> found) {
        WebDriver driver = context.getWrappedDriver();
        boolean isRetryable = !context.getCurrentTestCase().getSourceType().isSelenese();
        int timeout = context.getTimeout(); /* ms */
        while (true) {
            WebElement element = context.getElementFinder().findElementWithTimeout(driver, locator, isRetryable, timeout);
            try {
                if (context.isReplaceAlertMethod())
                    context.getJSLibrary().replaceAlertMethod(driver, element);
                return found.apply(element);
            } catch (StaleElementReferenceException e) {
                continue;
            }
        }
    }
}
