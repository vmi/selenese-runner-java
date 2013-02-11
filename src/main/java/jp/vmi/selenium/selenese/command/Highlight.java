package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.HighlightStyle;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "highlight".
 */
public class Highlight extends Command {

    private static final int LOCATOR = 0;

    Highlight(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1, NO_LOCATOR_INDEX);
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        testCase.getProc().highlight(args[LOCATOR], HighlightStyle.ELEMENT_STYLES[0]);
        return SUCCESS;
    }
}
