package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverCommandProcessor;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Commands implemented by {@link WebDriverCommandProcessor}.
 */
public class BuiltInCommand extends Command {

    private static final String WAIT_FOR_PAGE_TO_LOAD = "waitForPageToLoad";

    private static final String[] CANNOT_UPDATES = {
        "createCookie",
        "deleteCookie",
        "deleteAllVisibleCookies"
    };

    private final String realName;
    private final boolean andWait;
    private final boolean canUpdate;

    BuiltInCommand(int index, String name, String[] args, String realName, boolean andWait) {
        super(index, name, args, getArgumentCount(realName), getLocatorIndexes(realName));
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
        CustomCommandProcessor proc = testCase.getProc();
        try {
            Object result = proc.execute(realName, args);
            String resultString = (result != null) ? result.toString() : "";
            if (andWait) {
                int timeout = testCase.getRunner().getTimeout();
                proc.execute(WAIT_FOR_PAGE_TO_LOAD, new String[] { Integer.toString(timeout) });
            }
            return StringUtils.isNotEmpty(resultString) ? new Success(resultString) : SUCCESS;
        } catch (SeleniumException e) {
            String msg = e.getMessage().replaceAll("(\r?\n)+", " / ");
            return new Failure(msg);
        }
    }
}
