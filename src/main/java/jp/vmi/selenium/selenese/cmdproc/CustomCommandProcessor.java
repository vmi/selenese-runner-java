package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.webdriven.SeleneseCommand;
import com.thoughtworks.selenium.webdriven.WebDriverCommandProcessor;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.NullContext;
import jp.vmi.selenium.selenese.VarsMap;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;
import jp.vmi.selenium.selenese.subcommand.WDCommand;

/**
 * WebDriverCommandProcessor no timeout version.
 */
@Deprecated
public class CustomCommandProcessor extends WebDriverCommandProcessor {

    private final SubCommandMap subCommandMap;

    /**
     * Constructor.
     *
     * @param baseUrl base URL.
     * @param driver WebDriver instance.
     * @param varsMap variable map.
     */
    public CustomCommandProcessor(final String baseUrl, final WebDriver driver, final VarsMap varsMap) {
        super(baseUrl, driver); // dummy
        this.subCommandMap = new SubCommandMap(new NullContext() {

            @Override
            public String getCurrentBaseURL() {
                return baseUrl;
            }

            @Override
            public WebDriver getWrappedDriver() {
                return driver;
            }

            @Override
            public VarsMap getVarsMap() {
                return varsMap;
            }

            @Override
            public SubCommandMap getSubCommandMap() {
                return subCommandMap;
            }
        });
    }

    /**
     * Constructor.
     *
     * @param baseUrl base URL.
     * @param driver WebDriver instance.
     */
    public CustomCommandProcessor(String baseUrl, WebDriver driver) {
        this(baseUrl, driver, new VarsMap());
    }

    /**
     * Constructor.
     *
     * @param subCommandMap SubCommandMap instance.
     */
    public CustomCommandProcessor(SubCommandMap subCommandMap) {
        super("http://localhost", subCommandMap.getContext().getWrappedDriver()); // dummy
        this.subCommandMap = subCommandMap;
    }

    /**
     * Get SubCommandMap.
     *
     * @return SubCommandMap instance.
     */
    public SubCommandMap getProc() {
        return subCommandMap;
    }

    /**
     * Check the method existence.
     *
     * @param methodName method name.
     * @return true if the method exists.
     */
    @Override
    @Deprecated
    public boolean isMethodAvailable(String methodName) {
        return subCommandMap.get(methodName) != null;
    }

    /**
     * @see WDCommand#execute(Context, String...)
     *
     * @param commandName command name.
     * @param args arguments.
     * @return command result.
     */
    @Deprecated
    public Object execute(String commandName, String... args) {
        ISubCommand<?> command = subCommandMap.get(commandName);
        Context context = subCommandMap.getContext();
        return command.execute(context, args);
    }

    /**
     * Same as "context.getVarsMap().put(varName, value)".
     *
     * @param value value.
     * @param varName variable name.
     */
    @Deprecated
    public void setVar(Object value, String varName) {
        subCommandMap.setVar(value, varName);
    }

    @Override
    @Deprecated
    public String doCommand(String commandName, String[] args) {
        Object result = execute(commandName, args);
        if (result == null)
            return "";
        else if (result instanceof String)
            return (String) result;
        else
            return result.toString();
    }

    @Override
    @Deprecated
    public String getString(String commandName, String[] args) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public String[] getStringArray(String commandName, String[] args) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public Number getNumber(String commandName, String[] args) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public Number[] getNumberArray(String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean getBoolean(String commandName, String[] args) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean[] getBooleanArray(String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void addMethod(String methodName, SeleneseCommand<?> command) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public SeleneseCommand<?> getMethod(String methodName) {
        throw new UnsupportedOperationException();
    }
}
