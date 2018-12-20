package jp.vmi.selenium.selenese.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "setWindowSize".
 */
public class SetWindowSize extends AbstractCommand {

    private static final int ARG_RESOLUTION = 0;

    SetWindowSize(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    private final Pattern RESOLUTION_RE = Pattern.compile("(?<width>\\d+)x(?<height>\\d+)");

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String resolution = curArgs[ARG_RESOLUTION];
        Matcher matcher = RESOLUTION_RE.matcher(resolution);
        if (!matcher.matches())
            return new Error("Invalid resolution format. (e.g,. 1280x800)");
        int width = Integer.parseInt(matcher.group("width"));
        int height = Integer.parseInt(matcher.group("height"));
        WebDriver driver = context.getWrappedDriver();
        driver.manage().window().setSize(new Dimension(width, height));
        return SUCCESS;
    }
}
