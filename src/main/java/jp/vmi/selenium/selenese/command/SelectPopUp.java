package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.WindowSelector;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "selectPopUp".
 */
public class SelectPopUp extends AbstractCommand {

    private static final int ARG_WINDOW_ID = 0;

    SelectPopUp(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String windowID = curArgs[ARG_WINDOW_ID];
        String handle = WindowSelector.getInstance().selectPopUp(context, windowID);
        if (handle == null)
            return new Error("Specified pop up window not found: " + windowID);
        WindowSelector.waitAfterSelectingWindowIfNeed(context); // workaround.
        String title = context.getWrappedDriver().getTitle();
        return new Success("Selected pop up window [" + handle + "] " + title);
    }
}
