package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "endWhile".
 */
public class EndWhile extends Command implements EndLoop {

    private While startLoop;

    EndWhile(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 0, NO_LOCATOR_INDEX);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void setStartLoop(StartLoop startLoop) {
        this.startLoop = (While) startLoop;
    }

    @Override
    public Command next(TestCase testCase) {
        return startLoop;
    }
}
