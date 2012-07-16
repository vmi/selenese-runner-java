package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;

public class While extends Command implements StartLoop {

    private EndWhile endLoop;

    While(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public void setEndLoop(EndLoop endLoop) {
        this.endLoop = (EndWhile) endLoop;
    }

    @Override
    public Command next(Context context) {
        if (context.isTrue(args[0]))
            return next;
        else
            return endLoop.next;
    }
}
