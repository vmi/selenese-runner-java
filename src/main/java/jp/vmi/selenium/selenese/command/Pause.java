package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;

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
        if (StringUtils.isBlank(pauseMSec))
            return new Warning("pause is ignored: empty time.");
        try {
            Thread.sleep(Long.parseLong(pauseMSec));
            return new Success();
        } catch (NumberFormatException e) {
            return new Warning("pause is ignored: invalid time: " + pauseMSec);
        } catch (InterruptedException e) {
            return new Failure(e);
        }
    }
}
