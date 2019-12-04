package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.OptionLocator;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Select element.
 */
public class SelectElement {

    private final WebDriverElementFinder finder;

    private final WebElement select;
    private final Select selectUI;

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
        if (context.isReplaceAlertMethod())
            context.getJSLibrary().replaceAlertMethod(driver, select);
        selectUI = new Select(select);
        isMultiple = selectUI.isMultiple();
    }

    /**
     * Unset options if "select" is multiple.
     */
    public void unsetOptions() {
        selectUI.deselectAll();
    }

    /**
     * Select or remove the specified options.
     *
     * @param optionLocator option locator.
     * @param selectOrDeselect select the option if true, or deselect it if false.
     * @return result of selecting options.
     */
    public Result selectOptions(String optionLocator, boolean selectOrDeselect) {
        try {
            OptionLocator oLoc = new OptionLocator(optionLocator);
            switch (oLoc.type) {
            case "label":
                if (selectOrDeselect)
                    selectUI.selectByVisibleText(oLoc.arg);
                else
                    selectUI.deselectByVisibleText(oLoc.arg);
                break;
            case "value":
                if (selectOrDeselect)
                    selectUI.selectByValue(oLoc.arg);
                else
                    selectUI.deselectByValue(oLoc.arg);
                break;
            case "id":
                WebElement option = select.findElement(By.id(oLoc.arg));
                if (option.isSelected() ^ selectOrDeselect)
                    option.click();
                break;
            case "index":
                int index = Integer.parseInt(oLoc.arg);
                if (selectOrDeselect)
                    selectUI.selectByIndex(index);
                else
                    selectUI.deselectByIndex(index);
                break;
            }
        } catch (NoSuchElementException e) {
            String msg = e.getMessage();
            int nlIndex = msg.indexOf('\n');
            if (nlIndex > 0) {
                if (msg.charAt(nlIndex - 1) == '\r')
                    nlIndex--;
                msg = msg.substring(0, nlIndex);
            }
            return new Failure(msg);
        }
        return SUCCESS;
    }
}
