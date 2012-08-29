package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriverCommandProcessor;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Commands implemented by {@link WebDriverCommandProcessor}.
 */
public class BuiltInCommand extends Command {

    private static final String WAIT_FOR_PAGE_TO_LOAD = "waitForPageToLoad";
    private static final String WAIT_MSEC = "30000";

    private static final String[] CANNOT_UPDATES = {
        "createCookie",
        "deleteCookie",
        "deleteAllVisibleCookies"
    };

    private final String realName;
    private final boolean andWait;
    private final boolean canUpdate;

    BuiltInCommand(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args);
        this.realName = realName;
        this.andWait = andWait;
        this.canUpdate = !ArrayUtils.contains(CANNOT_UPDATES, realName);
    }

    @Override
    public boolean canUpdate() {
        return canUpdate;
    }

    @Override
    public Result doCommand(TestCase testCase) {
        String result = "";
        try {
            result = testCase.doBuiltInCommand(realName, testCase.replaceVariables(args));
            if (andWait)
                testCase.doBuiltInCommand(WAIT_FOR_PAGE_TO_LOAD, WAIT_MSEC);
            return StringUtils.isNotEmpty(result) ? new Success(result) : SUCCESS;
        } catch (SeleniumException e) {
            return new Failure("failed command:" + this.toString() + " result:" + e.getMessage());
        }
    }
}
