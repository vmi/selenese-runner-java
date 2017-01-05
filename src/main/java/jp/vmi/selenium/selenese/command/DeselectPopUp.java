package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.WindowSelector;

/**
 * Command "deselectPopUp".
 */
public class DeselectPopUp extends AbstractCommand {

    DeselectPopUp(int index, String name, String... args) {
        super(index, name, args);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String handle = WindowSelector.getInstance().selectPreviousWindow(context);
        if (handle == null)
            return new Error("Previous window not found");
        WindowSelector.waitAfterSelectingWindowIfNeed(context); // workaround.
        String title = context.getWrappedDriver().getTitle();
        return new Success("Re-selected window [" + handle + "] " + title);
    }
}
