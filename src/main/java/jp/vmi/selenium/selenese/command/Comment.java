package jp.vmi.selenium.selenese.command;

import jp.vmi.junit.result.ITestTarget;
import jp.vmi.junit.result.ITestTarget.Lifecycle;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.Result;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * <!-- comment -->
 */
public class Comment extends AbstractCommand {

    private static final String COMMENT_PREFIX = "### ";

    private static final String COMMENT_SUFFIX = " ###";

    private static final int MESSAGE = 0;

    Comment(int index, String name, String... args) {
        super(-1, name, args, VALUE);
    }

    @Override
    public boolean mayUpdateScreen() {
        return false;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        // if a comment contains FLAG_LIFECYCLE_DRAFT, set this testcase to DRAFT
        String comment = getArguments()[MESSAGE];
        if (comment.contains(ITestTarget.FLAG_LIFECYCLE_DRAFT)) {
            context.getCurrentTestCase().setLifecycle(Lifecycle.DRAFT);
        }
        return SUCCESS;
    }

    @Override
    public String toString() {
        return COMMENT_PREFIX + getArguments()[MESSAGE] + COMMENT_SUFFIX;
    }
}
