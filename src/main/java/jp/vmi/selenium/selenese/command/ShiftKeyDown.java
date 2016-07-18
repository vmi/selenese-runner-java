package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "shiftKeyDown".
 */
public class ShiftKeyDown extends AbstractCommand {

    ShiftKeyDown(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getModifierKeyState().shiftKeyDown();
        return SUCCESS;
    }
}
