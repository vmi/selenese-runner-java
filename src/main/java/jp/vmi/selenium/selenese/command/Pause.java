package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pause extends Command {

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
    public Result doCommand(Context context) {
        if (pausemsec.isEmpty()) {
            return new WarningResult("pause is ignored: empty time.");
        }

        try {
            Thread.sleep(Long.parseLong(pausemsec));
        } catch (NumberFormatException e) {
            return new WarningResult("pause is ignored: invalid time: " + pausemsec);
        } catch (InterruptedException e) {
            return new FailureResult(e);
        }
        return SUCCESS;
    }
}
