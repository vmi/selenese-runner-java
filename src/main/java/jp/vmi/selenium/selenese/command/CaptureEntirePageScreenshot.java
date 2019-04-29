package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.ScreenshotHandler;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.selenese.utils.LangUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "captureEntirePageScreenshot".
 */
public class CaptureEntirePageScreenshot extends AbstractCommand {

    private static final int ARG_FILENAME = 0;

    CaptureEntirePageScreenshot(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (!(context instanceof ScreenshotHandler))
            return new Success("captureEntirePageScreenshot is not supported.");
        String filename = curArgs[ARG_FILENAME];
        if (LangUtils.isBlank(filename))
            return new Warning("captureEntirePageScreenshot is ignored: empty filename.");
        ScreenshotHandler handler = (ScreenshotHandler) context;
        if (handler.isIgnoredScreenshotCommand())
            return new Success("captureEntirePageScreenshot is ignored.");
        try {
            addScreenshot(handler.takeEntirePageScreenshot(filename), "cmd");
            return SUCCESS;
        } catch (UnsupportedOperationException e) {
            return new Warning(e.getMessage());
        }
    }
}
