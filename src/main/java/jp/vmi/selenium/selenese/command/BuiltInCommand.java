package jp.vmi.selenium.selenese.command;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverCommandProcessor;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Failure;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;
import jp.vmi.selenium.selenese.subcommand.WDCommand;

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

    private final WDCommand command;
    private final boolean andWait;
    private final boolean canUpdate;

    BuiltInCommand(int index, String name, String[] args, WDCommand command, boolean andWait) {
        super(index, name, args, command.argumentCount, command.locatorIndexes);
        this.command = command;
        this.andWait = andWait;
        this.canUpdate = !ArrayUtils.contains(CANNOT_UPDATES, command.name);
    }

    @Override
    public boolean canUpdate() {
        return canUpdate;
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        SubCommandMap subCommandMap = runner.getSubCommandMap();
        try {
            String resultString = command.convertToString(command.execute(runner, args));
            if (andWait) {
                int timeout = runner.getTimeout();
                subCommandMap.getCommand(WAIT_FOR_PAGE_TO_LOAD).execute(runner, Integer.toString(timeout));
            }
            return StringUtils.isNotEmpty(resultString) ? new Success(resultString) : SUCCESS;
        } catch (SeleniumException e) {
            String msg = e.getMessage().replaceAll("(\r?\n)+", " / ");
            return new Failure(msg);
        }
    }
}
