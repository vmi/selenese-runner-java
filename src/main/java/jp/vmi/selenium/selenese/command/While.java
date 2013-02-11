package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "while".
 */
public class While extends Command implements StartLoop {

    private static final int CONDITION = 0;

    private EndWhile endLoop;

    While(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1, NO_LOCATOR_INDEX);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void setEndLoop(EndLoop endLoop) {
        this.endLoop = (EndWhile) endLoop;
    }

    @Override
    public Command next(TestCase testCase) {
        if (testCase.isTrue(args[CONDITION]))
            return next;
        else
            return endLoop.next;
    }
}
