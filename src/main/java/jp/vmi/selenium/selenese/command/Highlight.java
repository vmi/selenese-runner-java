package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.highlight.HighlightStyle;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "highlight".
 */
public class Highlight extends AbstractCommand {

    private static final int ARG_LOCATOR = 0;

    Highlight(int index, String name, String... args) {
        super(index, name, args, LOCATOR);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        if (context instanceof Runner)
            ((Runner) context).highlight(new Locator(curArgs[ARG_LOCATOR]), HighlightStyle.ELEMENT_STYLES[0]);
        return SUCCESS;
    }
}
