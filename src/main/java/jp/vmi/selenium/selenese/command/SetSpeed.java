package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Warning;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "setSpeed".
 */
public class SetSpeed extends Command {

    private static final int SPEED = 0;

    SetSpeed(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, 1);
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        String speed = args[SPEED];
        if (StringUtils.isBlank(speed))
            return new Warning("the argument of setSpeed is ignored: empty.");
        try {
            testCase.setSpeed(Long.parseLong(speed));
        } catch (NumberFormatException e) {
            return new Warning("the argument of setSpeed is ignored: invalid number format: " + speed);
        }
        return SUCCESS;
    }
}
