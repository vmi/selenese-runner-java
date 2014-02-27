package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "highlight".
 */
public class Highlight extends Command {

    private static final int LOCATOR = 0;

    Highlight(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        runner.highlight(args[LOCATOR], HighlightStyle.ELEMENT_STYLES[0]);
        return SUCCESS;
    }
}
