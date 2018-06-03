package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.MouseUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "clickAt".
 */
public class ClickAt extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_COORD = 1;

    ClickAt(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    private static Point coordToPoint(String coordString) {
        if (coordString.isEmpty())
            return new Point(0, 0);
        String[] pair = coordString.trim().split("\\s*,\\s*");
        int x = (int) Double.parseDouble(pair[0]);
        int y = (int) Double.parseDouble(pair[1]);
        return new Point(x, y);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        Point coord = coordToPoint(curArgs[ARG_COORD]);
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        context.getJSLibrary().replaceAlertMethod(driver, element);
        MouseUtils.moveTo(context, element, coord).click().perform();
        return SUCCESS;
    }
}
