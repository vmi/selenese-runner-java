package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor.*;

/**
 * Command "store".
 */
public class Store extends Command {

    private final String getter;
    private final String[] getterArgs;
    private final String varName;
    private final String[] cssLocator;

    Store(int index, String name, String[] args, String getter) {
        super(index, name, args, getArgumentCount(getter) + 1, getLocatorIndexes(getter));
        args = this.args;
        this.getter = getter;
        int len = args.length;
        getterArgs = Arrays.copyOf(args, len - 1);
        varName = args[len - 1];
        // "getAttribute" has a special locator argument.
        // Please check Assertion.java if want to modify following code.
        if ("getAttribute".equals(getter)) {
            int at = locators[0].lastIndexOf('@');
            if (at >= 0)
                locators[0] = locators[0].substring(0, at);
        }
        if (getter.equalsIgnoreCase("getCssCount"))
            cssLocator = new String[] { "css=" + args[0] };
        else
            cssLocator = null;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public String[] getLocators() {
        return cssLocator != null ? cssLocator : super.getLocators();
    }

    @Override
    protected Result doCommandImpl(TestCase testCase, Runner runner) {
        SeleneseRunnerCommandProcessor proc = testCase.getProc();
        Object result = proc.execute(getter, getterArgs);
        proc.setVar(result, varName);
        return new Success(proc.convertToString(result));
    }
}
