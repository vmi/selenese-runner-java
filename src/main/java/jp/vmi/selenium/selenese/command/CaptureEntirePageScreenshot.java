package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "captureEntirePageScreenshot".
 */
public class CaptureEntirePageScreenshot extends Command {

    private String filename = "";

    // not handle this parameter.
    @SuppressWarnings("unused")
    private String kwargs = "";

    CaptureEntirePageScreenshot(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
        if (args.length >= 1)
            filename = args[0];
        if (args.length >= 2)
            kwargs = args[1];
    }

    @Override
    public Result doCommand(TestCase testCase) {
        if (StringUtils.isBlank(filename))
            return new Warning("captureEntirePageScreenshot is ignored: empty filename.");
        try {
            testCase.getRunner().takeScreenshot(filename);
            return SUCCESS;
        } catch (UnsupportedOperationException e) {
            return new Warning(e.getMessage());
        }
    }
}
