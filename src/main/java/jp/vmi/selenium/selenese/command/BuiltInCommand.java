package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverCommandProcessor;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor.*;
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
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        SeleneseRunnerCommandProcessor proc = runner.getProc();
        try {
            String resultString = proc.convertToString(proc.execute(realName, args));
            if (andWait) {
                int timeout = runner.getTimeout();
                proc.execute(WAIT_FOR_PAGE_TO_LOAD, Integer.toString(timeout));
            }
            return StringUtils.isNotEmpty(resultString) ? new Success(resultString) : SUCCESS;
        } catch (SeleniumException e) {
            String msg = e.getMessage().replaceAll("(\r?\n)+", " / ");
            return new Failure(msg);
        }
    }
}
