package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;

public class EndFor extends Command implements EndLoop {

    private StoreFor startLoop;

    EndFor(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public void setStartLoop(StartLoop startLoop) {
        this.startLoop = (StoreFor) startLoop;
    }

    @Override
    public Command next(Context context) {
        return startLoop;
    }
}
