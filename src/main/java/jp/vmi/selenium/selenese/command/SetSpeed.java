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

    private String speed = null;

    SetSpeed(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
        if (args.length >= 1)
            speed = args[0];
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        if (StringUtils.isBlank(speed))
            return new Warning("the argument of setSpeed is ignored: empty.");
        try {
            testCase.setSpeed(Long.parseLong(args[SPEED]));
        } catch (NumberFormatException e) {
            return new Warning("the argument of setSpeed is ignored: invalid number format: " + speed);
        }
        return SUCCESS;
    }
}
