package jp.vmi.selenium.selenese.command;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.result.Success.*;

public class CaptureEntirePageScreenshot extends Command {

    private static final Logger log = LoggerFactory.getLogger(CaptureEntirePageScreenshot.class);

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
        if (filename.isEmpty())
            return new Warning("captureEntirePageScreenshot is ignored: empty filename.");
        if (testCase.getProc().getWrappedDriver() instanceof TakesScreenshot) {
            TakesScreenshot screenshottaker = (TakesScreenshot) testCase.getProc().getWrappedDriver();
            File tmp = screenshottaker.getScreenshotAs(OutputType.FILE);
            if (!tmp.renameTo(new File(filename)))
                log.warn("fail to rename file to:" + filename);
            return SUCCESS;
        } else {
            return new Warning("webdriver is not support taking screenshot.");
        }
    }
}
