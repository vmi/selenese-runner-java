package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.MaxTimeActiveTimer;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.MaxTimeExceeded;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;
import jp.vmi.selenium.selenese.utils.LangUtils;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "pause".
 */
public class Pause extends AbstractCommand {

    private static final int ARG_PAUSE_MSEC = 0;

    Pause(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String pauseMSec = curArgs[ARG_PAUSE_MSEC];
        if (LangUtils.isBlank(pauseMSec))
            return new Warning("pause is ignored: empty time.");
        try {
            Thread.sleep(Long.parseLong(pauseMSec));
            return SUCCESS;
        } catch (NumberFormatException e) {
            return new Warning("pause is ignored: invalid time: " + pauseMSec);
        } catch (InterruptedException e) {
            if (MaxTimeActiveTimer.isInterruptedByMaxTimeTimer(Thread.currentThread()))
                return new MaxTimeExceeded(e);
            else
                return new Failure(e);
        }
    }
}
