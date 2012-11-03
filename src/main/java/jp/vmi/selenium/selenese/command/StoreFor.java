package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "storeFor".
 */
public class StoreFor extends Command implements StartLoop {

    private EndFor endLoop;

    StoreFor(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
    }

    @Override
    public boolean canUpdate() {
        return false;
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
        testCase.getProc().setVar(value, args[1]);
        return next;
    }
}
