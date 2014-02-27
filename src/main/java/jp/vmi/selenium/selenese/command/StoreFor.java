package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "storeFor".
 */
public class StoreFor extends Command implements StartLoop {

    private static final int COLLECTION_NAME = 0;
    private static final int VAR_NAME = 1;

    private EndFor endLoop;

    StoreFor(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 2);
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
    public Command next(TestCase testCase, Runner runner) {
        String value = runner.getCollectionMap().pollFromCollection(args[COLLECTION_NAME]);
        if (value == null)
            return endLoop.next;
        runner.getVarsMap().put(args[VAR_NAME], value);
        return next;
    }
}
