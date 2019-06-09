package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.ElementNotInteractableException;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "click".
 */
public class Click extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    Click(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String locator = curArgs[ARG_LOCATOR];
        return ClickHandler.handleClick(context, locator, element -> {
            try {
                element.click();
                return SUCCESS;
            } catch (ElementNotInteractableException e) {
                context.executeScript("arguments[0].click()", element);
                return new Success("Success (the element is not visible)");

            }
        });
    }
}
