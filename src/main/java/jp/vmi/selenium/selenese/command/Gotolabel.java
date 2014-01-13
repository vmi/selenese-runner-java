package jp.vmi.selenium.selenese.command;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Error;

/**
 * Command "gotolabel".
 */
public class Gotolabel extends Command {

    private static final int LABEL = 0;

    Gotolabel(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Command next(TestCase testCase, Runner runner) {
        Label labelCommand = testCase.getLabelCommand(args[LABEL]);
        if (labelCommand == null) {
            String msg = "label \"" + args[LABEL] + "\" is not found.";
            setResult(new Error(msg));
            throw new SeleniumException(msg);
        }
        return labelCommand.next;
    }
}
