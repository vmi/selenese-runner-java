package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "answerOnNextNativeAlert".
 */
public class AnswerOnNextNativeAlert extends AbstractCommand {

    private static final int ARG_ANSWER = 0;

    AnswerOnNextNativeAlert(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        context.getNextNativeAlertActionListener().setAnswer(curArgs[ARG_ANSWER]);
        return SUCCESS;
    }
}
