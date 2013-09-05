package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Runner;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "pause".
 */
public class Pause extends Command {

    private static final int PAUSE_MSEC = 0;

    Pause(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        String pausemsec = args[PAUSE_MSEC];
        if (pausemsec.isEmpty()) {
            return new Warning("pause is ignored: empty time.");
        }

        try {
            Thread.sleep(Long.parseLong(pausemsec));
        } catch (NumberFormatException e) {
            return new Warning("pause is ignored: invalid time: " + pausemsec);
        } catch (InterruptedException e) {
            return new Failure(e);
        }
        return SUCCESS;
    }
}
