package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.SeleniumUtils;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "windowMaximize".
 */
public class WindowMaximize extends AbstractCommand {

    WindowMaximize(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        WebDriver driver = context.getWrappedDriver();
        SeleniumUtils.windowMaximize(driver);
        return SUCCESS;
    }
}
