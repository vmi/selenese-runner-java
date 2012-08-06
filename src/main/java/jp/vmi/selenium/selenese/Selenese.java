package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.Command.Result;

public interface Selenese {

    String getName();

    Result execute(Runner runner);
}
