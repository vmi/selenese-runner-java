package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "storeFor".
 */
public class StoreFor extends Command implements StartLoop {

    private static final int COLLECTION_NAME = 0;
    private static final int VAR_NAME = 1;

    private EndFor endLoop;

    StoreFor(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 2, NO_LOCATOR_INDEX);
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
        String value = testCase.pollFromCollection(args[COLLECTION_NAME]);
        if (value == null)
            return endLoop.next;
        testCase.getProc().setVar(value, args[VAR_NAME]);
        return next;
    }
}
