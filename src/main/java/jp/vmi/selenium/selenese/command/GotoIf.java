package jp.vmi.selenium.selenese.command;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.TestCase;

/**
 * Command "gotoIf".
 */
public class GotoIf extends Command {

    private static final int EXPRESSION = 0;
    private static final int LABEL = 1;

    GotoIf(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 2);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Command next(TestCase testCase) {
        if (!testCase.isTrue(args[EXPRESSION]))
            return next;
        Label labelCommand = testCase.getLabelCommand(args[LABEL]);
        if (labelCommand == null)
            throw new SeleniumException("label \"" + args[LABEL] + "\" is not found.");
        return labelCommand.next;
    }
}
