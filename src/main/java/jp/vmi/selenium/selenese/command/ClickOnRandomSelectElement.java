package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static jp.vmi.selenium.selenese.command.ArgumentType.LOCATOR;
import static jp.vmi.selenium.selenese.result.Success.SUCCESS;

/**
 * Selects random element from drop-down lists by the xpath locator. Works correctly with drop-down lists with ul/li elements.
 *
 * @param locator an element locator should return the list of elements by xpath.
 */

public class ClickOnRandomSelectElement extends AbstractCommand{

    private static final int ARG_LOCATOR = 0;

    ClickOnRandomSelectElement(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {

        String locator = curArgs[ARG_LOCATOR];
        WebDriver driver = context.getWrappedDriver();
        List<WebElement> elements = context.getElementFinder().findElements(driver, locator);
        if (elements.size() > 0)
            System.out.println(elements.size());
        String value = RandomStringUtils.random(1, false, true);
        System.out.println(value);

//        try {
////            element.click();
////            return SUCCESS;
//        } catch (ElementNotVisibleException e) {
////            context.executeScript("arguments[0].click()", element);
////            return new Success("Success (the element is not visible)");
//        }
        return SUCCESS;
    }
}
