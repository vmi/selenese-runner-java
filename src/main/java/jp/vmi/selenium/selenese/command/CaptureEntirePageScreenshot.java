package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.result.Warning;

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
        String filename = curArgs[ARG_FILENAME];
        if (!(context instanceof Runner))
            return new Success("captureEntirePageScreenshot is not supported.");
        Runner runner = (Runner) context;
        if (runner.isIgnoreScreenshotCommand()) {
            return new Success("captureEntirePageScreenshot is ignored.");
        } else if (StringUtils.isBlank(filename)) {
            return new Warning("captureEntirePageScreenshot is ignored: empty filename.");
        } else {
            try {
                runner.takeScreenshot(filename, runner.getCurrentTestCase());
                return SUCCESS;
            } catch (UnsupportedOperationException e) {
                return new Warning(e.getMessage());
            }
        }
    }
}
