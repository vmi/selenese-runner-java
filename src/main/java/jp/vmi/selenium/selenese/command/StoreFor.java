package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;

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
    public Command next(TestCase testCase) {
        String value = testCase.pollFromCollection(args[0]);
        if (value == null)
            return endLoop.next;
        testCase.setVariable(value, args[1]);
        return next;
    }
}
