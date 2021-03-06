package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "debugger".
 *
 * <p>
 * This command has no effect, and is handled in "jp.vmi.selenium.selenese.SimpleInteractiveModeHandler".
 * </p>
 */
public class Debugger extends AbstractCommand {

    Debugger(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        return SUCCESS;
    }
}
