package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Command "GetElementBackgroundColor".
 */

public class GetElementBackgroundColor {

    private final WebDriverElementFinder finder;
    public final WebElement element;

    public GetElementBackgroundColor(Context context, String selectLocator) {
        WebDriver driver = context.getWrappedDriver();
        finder = context.getElementFinder();
        element = finder.findElement(driver, selectLocator);
        String color = element.getAttribute("color");
        System.out.println(color);
    }
}
