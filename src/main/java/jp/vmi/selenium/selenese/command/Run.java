package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.CommandResult;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "run".
 */
public class Run extends AbstractCommand {

    private static final int ARG_TEST_CASE_NAME = 0;

    Run(int index, String name, String... args) {
        super(index, name, args, VALUE);
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String testCaseName = curArgs[ARG_TEST_CASE_NAME];
        TestCase testCase = context.getTestCaseMap().get(testCaseName);
        if (testCase == null)
            return new Error("Missing test-case: " + testCaseName);
        TestCase current = context.getCurrentTestCase();
        String seq = context.getCommandListIterator().getCommandSequence().toString();
        StartMarker marker = new StartMarker(this, "Start: " + testCaseName);
        long now = System.currentTimeMillis();
        CommandResult markerResult = new CommandResult(seq, marker, marker.getScreenshots(), marker.getResult(), now, now);
        current.getResultList().add(markerResult);
        Result result = testCase.execute(current, context);
        return result == SUCCESS ? new Success("Success: " + testCaseName) : result;
    }
}
