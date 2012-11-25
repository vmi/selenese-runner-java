package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "open".
 */
public class Open extends Command {

    private static final int URL = 0;

    Open(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    public Result doCommand(TestCase testCase) {
        String url = args[URL];
        if (!url.contains("://"))
            url = testCase.getBaseURL() + (url.startsWith("/") ? "" : "/") + url;
        testCase.getRunner().getDriver().get(url);
        return SUCCESS;
    }
}
