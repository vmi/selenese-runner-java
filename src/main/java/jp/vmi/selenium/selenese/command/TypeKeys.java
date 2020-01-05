package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "typeKeys".
 * (alias: "sendKeys" and "keyPress")
 */
public class TypeKeys extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;
    private static final int ARG_VALUE = 1;

    TypeKeys(int index, String name, String... args) {
        super(index, name, args, LOCATOR, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        String value = curArgs[ARG_VALUE];
        value = value.replace("\\10", Keys.ENTER);
        value = value.replace("\\13", Keys.RETURN);
        value = value.replace("\\27", Keys.ESCAPE);
        value = value.replace("\\38", Keys.ARROW_UP);
        value = value.replace("\\40", Keys.ARROW_DOWN);
        value = value.replace("\\37", Keys.ARROW_LEFT);
        value = value.replace("\\39", Keys.ARROW_RIGHT);
        WebDriver driver = context.getWrappedDriver();
        WebElement element = context.getElementFinder().findElement(driver, locator);
        if (context.isReplaceAlertMethod())
            context.getJSLibrary().replaceAlertMethod(driver, element);
        element.sendKeys(value);
        return SUCCESS;
    }
}
