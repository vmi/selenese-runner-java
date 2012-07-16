package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;

public class EndWhile extends Command implements EndLoop {

    private While startLoop;

    EndWhile(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public void setStartLoop(StartLoop startLoop) {
        this.startLoop = (While) startLoop;
    }

    @Override
    public Command next(Context context) {
        return startLoop;
    }
}
