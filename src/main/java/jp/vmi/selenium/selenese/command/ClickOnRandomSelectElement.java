package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

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
    //        var xPathRes = this.browserbot.getDocument().evaluate(locator, this.browserbot.getDocument(), null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
//        if (xPathRes.snapshotLength > 0) {
//            var randomNum = Math.floor(2 + Math.random() * (xPathRes.snapshotLength - 1));
//            var randomElem = locator + '[' + randomNum + ']';
//            this.doClickAt(randomElem);
//        } else throw new SeleniumError('Locator does not exist');
//    };

    // Fix me
    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        WebDriver driver = context.getWrappedDriver();
        List<WebElement> elements = context.getElementFinder().findElements(driver, locator);
        int randomNum = 0;
        if (elements.size() > 0) {
            randomNum = new Random().nextInt((elements.size() - 1) + 1);
            WebElement element = context.getElementFinder().findElement(driver, locator + '[' + randomNum + ']');
            context.getJSLibrary().replaceAlertMethod(driver, element);
            try {
                element.click();
                return SUCCESS;
            } catch (ElementNotVisibleException e) {
                context.executeScript("arguments[0].click()", element);
                return new Success("Success (the element is not visible)");
            }
        }
        return SUCCESS;
    }
}



