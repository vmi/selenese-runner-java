package jp.vmi.selenium.selenese.command;

import java.util.Arrays;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;
import jp.vmi.selenium.selenese.subcommand.WDCommand;

/**
 * Command "store".
 */
public class Store extends Command {

    private final WDCommand getterCommand;
    private final String[] getterArgs;
    private final String varName;
    private final String[] cssLocator;

    Store(int index, String name, String[] args, WDCommand getterCommand) {
        super(index, name, args, getterCommand.argumentCount + 1, getterCommand.locatorIndexes);
        args = this.args;
        this.getterCommand = getterCommand;
        int len = args.length;
        getterArgs = Arrays.copyOf(args, len - 1);
        varName = args[len - 1];
        // "getAttribute" has a special locator argument.
        // Please check Assertion.java if want to modify following code.
        if ("getAttribute".equalsIgnoreCase(getterCommand.name)) {
            int at = locators[0].lastIndexOf('@');
            if (at >= 0)
                locators[0] = locators[0].substring(0, at);
        }
        if ("getCssCount".equalsIgnoreCase(getterCommand.name))
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
        Object result = getterCommand.execute(runner, getterArgs);
        runner.getVarsMap().put(varName, result);
        return new Success(getterCommand.convertToString(result));
    }
}
