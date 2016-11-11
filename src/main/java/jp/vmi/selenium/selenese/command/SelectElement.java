package jp.vmi.selenium.selenese.command;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.OptionLocator;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Select element.
 */
public class SelectElement {

    private final WebDriverElementFinder finder;

    /** "select" element. */
    public final WebElement select;

    /** true if "select" element is multiple select box. */
    public final boolean isMultiple;

    /**
     * Constructor.
     *
     * @param context context.
     * @param selectLocator select locator.
     */
    public SelectElement(Context context, String selectLocator) {
        WebDriver driver = context.getWrappedDriver();
        finder = context.getElementFinder();
        select = finder.findElement(driver, selectLocator);
        context.getJSLibrary().replaceAlertMethod(driver, select);
        String multiple = select.getAttribute("multiple");
        isMultiple = multiple != null && (multiple.equalsIgnoreCase("true") || multiple.equalsIgnoreCase("multiple"));
    }

    /**
     * Unset options if "select" is multiple.
     */
    public void unsetOptions() {
        for (WebElement option : select.findElements(By.tagName("option")))
            if (option.isSelected())
                option.click();
    }

    /**
     * Select or remove the specified options.
     *
     * @param optionLocator option locator.
     * @param doSelect do select the option if true, or do remove if false.
     */
    public void selectOptions(String optionLocator, boolean doSelect) {
        List<WebElement> options = finder.findOptions(select, new OptionLocator(optionLocator));
        for (WebElement option : options)
            if (doSelect ^ option.isSelected())
                option.click();
    }
}
