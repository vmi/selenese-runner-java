package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.Command.Result;

public interface Selenese {

    Result execute(Runner runner);
}
