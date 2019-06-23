package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.utils.Wait;
import jp.vmi.selenium.selenese.utils.WindowSelector;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

/**
 * Command "waitForPopUp".
 */
public class WaitForPopUp extends AbstractCommand {

    private static final int ARG_WINDOW_ID = 0;
    private static final int ARG_TIMEOUT = 1;

    WaitForPopUp(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    @Override
    protected Result executeImpl(final Context context, String... curArgs) {
        final String windowID = curArgs[ARG_WINDOW_ID];
        String argTimeout = curArgs[ARG_TIMEOUT];
        long timeout;
        if (argTimeout == null || argTimeout.isEmpty())
            timeout = context.getTimeout();
        else
            timeout = Long.valueOf(argTimeout);
        long startTime = System.currentTimeMillis();
        final String[] selectedHandle = new String[1];
        boolean result = Wait.defaultInterval.wait(startTime, timeout, () -> {
            String handle = WindowSelector.getInstance().selectPopUp(context, windowID);
            if (handle == null)
                return false;
            WindowSelector.waitAfterSelectingWindowIfNeed(context); // workaround.
            selectedHandle[0] = handle;
            return true;
        });
        if (!result)
            return new Error("Specified pop up window not found: " + windowID);
        String title = context.getWrappedDriver().getTitle();
        return new Success("Selected pop up window [" + selectedHandle[0] + "] " + title);
    }
}
