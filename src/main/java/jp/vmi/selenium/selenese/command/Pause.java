package jp.vmi.selenium.selenese.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.result.Result.*;

public class Pause extends Command {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Pause.class);

    private String pausemsec = "";

    // not handle this parameter.
    @SuppressWarnings("unused")
    private String kwargs = "";

    Pause(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
        if (args.length >= 1) {
            pausemsec = args[0];
        }
        if (args.length >= 2) {
            kwargs = args[1];
        }
    }

    @Override
    public Result doCommand(TestCase testCase) {
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
