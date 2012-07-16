package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.StringUtils;

import jp.vmi.selenium.selenese.Context;

public class BuiltInCommand extends Command {

    private static final String WAIT_FOR_PAGE_TO_LOAD = "waitForPageToLoad";
    private static final String WAIT_MSEC = "30000";

    private final String realName;
    private final boolean andWait;

    BuiltInCommand(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
        this.realName = realName;
        this.andWait = andWait;
    }

    @Override
    public Result doCommand(Context context) {
        String result = context.doCommand(realName, context.replaceVariables(args));
        if (andWait)
            context.doCommand(WAIT_FOR_PAGE_TO_LOAD, WAIT_MSEC);
        return StringUtils.isNotEmpty(result) ? new Result(true, result) : SUCCESS;
    }
}
