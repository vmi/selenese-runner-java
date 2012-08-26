package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.Ignore;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.inject.DoCommand;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LoggerUtils;

import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * test-case object for execution.
 * <p>
 * Supports Selenium IDE flow control plugin.
 * </p>
 * @see <a href="https://github.com/davehunt/selenium-ide-flowcontrol">A flow control plugin for Selenium IDE</a>
 */
@Ignore
public class TestCase implements Selenese, ITestCase {

    private static final Logger log = LoggerFactory.getLogger(TestCase.class);

    private File file = null;
    private String name = null;
    private String baseURL = null;
    private WebDriverCommandProcessor proc = null;
    private WebDriver driver = null;

    private final Map<String, String> variableMap = new HashMap<String, String>();
    private final Map<String, Deque<String>> collectionMap = new HashMap<String, Deque<String>>();
    private final Map<String, Label> labelCommandMap = new HashMap<String, Label>();

    private final CommandList commandList = new CommandList();
    private Command prev = commandList;

    /**
     * Initialize after constructed.
     *
     * @param file selenese script file.
     * @param name test-case name.
     * @param driver target WebDriver instance.
     * @param baseURL effective base URL.
     * @return this.
     */
    public TestCase initialize(File file, String name, WebDriver driver, String baseURL) {
        this.file = file;
        this.name = name;
        this.driver = driver;
        this.baseURL = baseURL;
        if (driver != null)
            this.proc = new WebDriverCommandProcessor(baseURL, driver);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get WebDriverCommandProcessor generated at initialize.
     *
     * @return WebDriverCommandProcessor.
     */
    public WebDriverCommandProcessor getProc() {
        return proc;
    }

    /**
     * Get WebDriver.
     *
     * @return WebDriver.
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Get base URL.
     *
     * @return base URL.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Run built-in command of WebDriverCommandProcessor.
     *
     * @param name command name.
     * @param args arguments.
     * @return result.
     */
    public String doBuiltInCommand(String name, String... args) {
        try {
            return proc.doCommand(name, args);
        } catch (UnsupportedOperationException e) {
            throw new SeleniumException("No such command: " + name);
        } catch (SeleniumException e) {
            log.error("{}({})", name, StringUtils.join(LoggerUtils.quote(args), ", "));
            throw e;
        }
    }

    /**
     * Run built-in command of WebDriverCommandProcessor. It returns boolean.
     *
     * @param name command name.
     * @param args arguments.
     * @return result.
     */
    public boolean isBuiltInCommand(String name, String... args) {
        try {
            return proc.getBoolean(name, args);
        } catch (UnsupportedOperationException e) {
            throw new SeleniumException("No such command: " + name);
        } catch (SeleniumException e) {
            log.error("{}({})", name, StringUtils.join(LoggerUtils.quote(args), ", "));
            throw e;
        }
    }

    /**
     * Set variable value.
     *
     * @param value value.
     * @param varName variable name.
     */
    public void setVariable(String value, String varName) {
        variableMap.put(varName, value);
    }

    /**
     * Replace variable reference to value.
     *
     * @param expr expression string.
     * @return replaced string.
     */
    public String replaceVariables(String expr) {
        StrSubstitutor s = new StrSubstitutor(variableMap);
        return s.replace(expr);
    }

    /**
     * Replace variable reference to value for each strings.
     *
     * @param exprs expression strings.
     * @return replaced strings.
     */
    public String[] replaceVariables(String[] exprs) {
        String[] result = new String[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            result[i] = replaceVariables(exprs[i]);
        return result;
    }

    /**
     * Evaluate expression and return boolean result.
     *
     * @param expr expression string.
     * @return result.
     */
    public boolean isTrue(String expr) {
        return Boolean.parseBoolean(proc.doCommand("getEval", new String[] { replaceVariables(expr) }));
    }

    /**
     * Create new collection (FIFO).
     *
     * @param collectionName collection name.
     */
    public void addCollection(String collectionName) {
        collectionMap.put(collectionName, new ArrayDeque<String>());
    }

    /**
     * Add value to collection.
     *
     * @param collectionName collection name.
     * @param value value.
     */
    public void addToCollection(String collectionName, String value) {
        Deque<String> collection = collectionMap.get(collectionName);
        collection.addLast(value);
    }

    /**
     * Poll value from collection.
     *
     * @param collectionName collection name.
     * @return value.
     */
    public String pollFromCollection(String collectionName) {
        Deque<String> collection = collectionMap.get(collectionName);
        return collection.pollFirst();
    }

    /**
     * Register label command.
     *
     * @param labelCommand label command.
     */
    public void setLabelCommand(Label labelCommand) {
        labelCommandMap.put(labelCommand.getLabel(), labelCommand);
    }

    /**
     * Get label by name.
     *
     * @param label label name.
     * @return label command.
     */
    public Label getLabelCommand(String label) {
        return labelCommandMap.get(label);
    }

    /**
     * Add command to command list.
     *
     * @param command command.
     */
    public void addCommand(Command command) {
        prev = prev.setNext(command);
    }

    @DoCommand
    protected Result doCommand(Command command) {
        return command.doCommand(this);
    }

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent, Runner runner) {
        Command current = commandList.first();
        Result totalResult = SUCCESS;
        while (current != null) {
            Result result = doCommand(current);
            runner.takeScreenshotAll(current.getIndex());
            totalResult = totalResult.update(result);
            if (totalResult.isInterrupted())
                break;
            current = current.next(this);
        }
        return totalResult;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TestCase[").append(name).append("]");
        if (file != null)
            s.append(" (").append(file).append(")");
        return s.toString();
    }
}
