package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "label".
 */
public class Label extends AbstractCommand implements ILabel {

    private static final int ARG_LABEL = 0;

    Label(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    /**
     * Get label string.
     *
     * @return label.
     */
    @Override
    public String getLabel() {
        return getArguments()[ARG_LABEL];
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        return SUCCESS;
    }
}
