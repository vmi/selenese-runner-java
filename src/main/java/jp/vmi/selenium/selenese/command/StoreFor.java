package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;

public class StoreFor extends Command implements StartLoop {

    private EndFor endLoop;

    StoreFor(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public void setEndLoop(EndLoop endLoop) {
        this.endLoop = (EndFor) endLoop;
    }

    @Override
    public Command next(Context context) {
        String value = context.pollFromCollection(args[0]);
        if (value == null)
            return endLoop.next;
        context.setVariable(value, args[1]);
        return next;
    }
}
