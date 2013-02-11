package jp.vmi.selenium.selenese.command;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "gotolabel".
 */
public class Gotolabel extends Command {

    private static final int LABEL = 0;

    Gotolabel(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1, NO_LOCATOR_INDEX);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Command next(TestCase testCase) {
        Label labelCommand = testCase.getLabelCommand(args[LABEL]);
        if (labelCommand == null)
            throw new SeleniumException("label \"" + args[LABEL] + "\" is not found.");
        return labelCommand.next;
    }
}
