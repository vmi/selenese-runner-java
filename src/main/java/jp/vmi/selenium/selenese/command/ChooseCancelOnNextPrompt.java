package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.javascript.JSLibrary;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "chooseCancelOnNextPrompt".
 */
public class ChooseCancelOnNextPrompt extends AbstractCommand {

    ChooseCancelOnNextPrompt(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        WebDriver driver = context.getWrappedDriver();
        JSLibrary jsLibrary = context.getJSLibrary();
        jsLibrary.replaceAlertMethod(driver, null);
        jsLibrary.answerOnNextPrompt(driver, null);
        return SUCCESS;
    }
}
