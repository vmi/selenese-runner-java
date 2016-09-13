package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "clickAt".
 */
public class ClickAt extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_COORD = 1;

    ClickAt(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    private static Point coordToPoint(String coordString) {
        String[] pair = coordString.trim().split("\\s*,\\s*");
        int x = (int) Double.parseDouble(pair[0]);
        int y = (int) Double.parseDouble(pair[1]);
        return new Point(x, y);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        Point coord;
        if (curArgs.length > ARG_COORD)
            coord = coordToPoint(curArgs[ARG_COORD]);
        else
            coord = new Point(0, 0);
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        context.getDialogOverride().replaceAlertMethod(driver, element);
        new Actions(driver).moveToElement(element, coord.x, coord.y).click().perform();
        return SUCCESS;
    }
}
