package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.NullContext;
import jp.vmi.selenium.selenese.VarsMap;

/**
 * WebDriverCommandProcessor no timeout version.
 */
@Deprecated
public class CustomCommandProcessor extends WebDriverCommandProcessor {

    private final SeleneseRunnerCommandProcessor proc;

    /**
     * Constructor.
     *
     * @param baseUrl base URL.
     * @param driver WebDriver instance.
     * @param varsMap variable map.
     */
    public CustomCommandProcessor(final String baseUrl, final WebDriver driver, final VarsMap varsMap) {
        super(baseUrl, driver); // dummy
        this.proc = new SeleneseRunnerCommandProcessor(new NullContext() {

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
            public SeleneseRunnerCommandProcessor getProc() {
                return proc;
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
     * @param proc SeleneseRunnerCommandProcessor instance. 
     */
    public CustomCommandProcessor(SeleneseRunnerCommandProcessor proc) {
        super("http://localhost", proc.getContext().getWrappedDriver()); // dummy
        this.proc = proc;
    }

    /**
     * Get SeleneseRunnerCommandProcessor.
     * 
     * @return SeleneseRunnerCommandProcessor instance.
     */
    public SeleneseRunnerCommandProcessor getProc() {
        return proc;
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
        return proc.getCommand(methodName) != null;
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
        WDCommand command = proc.getCommand(commandName);
        Context context = proc.getContext();
        return command.execute(context, args);
    }

    /**
     * @see SeleneseRunnerCommandProcessor#setVar(Object, String)
     * 
     * @param value value.
     * @param varName variable name.
     */
    @Deprecated
    public void setVar(Object value, String varName) {
        proc.setVar(value, varName);
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
        throw new UnsupportedOperationException("getString");
    }

    @Override
    @Deprecated
    public String[] getStringArray(String commandName, String[] args) {
        throw new UnsupportedOperationException("getStringArray");
    }

    @Override
    @Deprecated
    public Number getNumber(String commandName, String[] args) {
        throw new UnsupportedOperationException("getNumber");
    }

    @Override
    @Deprecated
    public Number[] getNumberArray(String s, String[] strings) {
        throw new UnsupportedOperationException("getNumberArray");
    }

    @Override
    @Deprecated
    public boolean getBoolean(String commandName, String[] args) {
        throw new UnsupportedOperationException("getBoolean");
    }

    @Override
    @Deprecated
    public boolean[] getBooleanArray(String s, String[] strings) {
        throw new UnsupportedOperationException("getBooleanArray");
    }

    @Override
    @Deprecated
    public void addMethod(String methodName, SeleneseCommand<?> command) {
        throw new UnsupportedOperationException("addMethod");
    }

    @Override
    @Deprecated
    public SeleneseCommand<?> getMethod(String methodName) {
        throw new UnsupportedOperationException("getMethod");
    }
}
