package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Runner;
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
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        String url = testCase.getProc().replaceVars(args[URL]);
        if (!url.contains("://"))
            url = testCase.getBaseURL() + (url.startsWith("/") ? "" : "/") + url;
        runner.getDriver().get(url);
        return SUCCESS;
    }
}
