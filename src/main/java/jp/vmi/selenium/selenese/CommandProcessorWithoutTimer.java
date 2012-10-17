package jp.vmi.selenium.selenese;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

/**
 * WebDriverCommandProcessor no timeout version.
 */
public class CommandProcessorWithoutTimer extends WebDriverCommandProcessor {

    /**
     * Constructor.
     *
     * @param driver WebDriver instance.
     */
    public CommandProcessorWithoutTimer(WebDriver driver) {
        // baseUrl is only used in "open" command.
        // "open" command is re-implemented.
        super("", driver);
    }

    @Override
    public String doCommand(String commandName, String[] args) {
        Object val = execute(commandName, args);
        return val != null ? val.toString() : null;
    }

    @Override
    public String getString(String commandName, String[] args) {
        return (String) execute(commandName, args);
    }

    @Override
    public String[] getStringArray(String commandName, String[] args) {
        return (String[]) execute(commandName, args);
    }

    @Override
    public Number getNumber(String commandName, String[] args) {
        return (Number) execute(commandName, args);
    }

    @Override
    public boolean getBoolean(String commandName, String[] args) {
        return (Boolean) execute(commandName, args);
    }

    private Object execute(String commandName, final String[] args) {
        final SeleneseCommand<?> command = getMethod(commandName);
        if (command == null)
            throw new UnsupportedOperationException(commandName);
        return command.apply(getWrappedDriver(), args);
    }

}
