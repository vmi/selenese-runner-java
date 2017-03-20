package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static jp.vmi.selenium.selenese.command.ArgumentType.LOCATOR;

/**
 * Command "GetElementByBackgroundColor".
 */

public abstract class GetElementByBackgroundColor extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    /**
     * Constructor.
     *
     * @param index    command index.
     * @param name     command name.
     * @param args     command args.
     * @param argTypes command argument types.
     */
    public GetElementByBackgroundColor(int index, String name, String[] args, ArgumentType... argTypes) {
        super(index, name, args, LOCATOR);
    }

    public String getResult(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        String color = element.getCssValue("backgroundColor");
        System.out.println(color);
        return color;
    }
}
