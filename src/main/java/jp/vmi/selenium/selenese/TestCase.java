package jp.vmi.selenium.selenese;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.cmdproc.CustomCommandProcessor;
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
public class TestCase implements Selenese, ITestCase {

    private static final Logger log = LoggerFactory.getLogger(TestCase.class);

    private File file = null;
    private String basename = null;
    private String name = null;
    private Runner runner = null;
    private String baseURL = null;
    private long speed = 0;

    private CustomCommandProcessor proc = null;

    private final Map<String, Deque<String>> collectionMap = new HashMap<String, Deque<String>>();
    private final Map<String, Label> labelCommandMap = new HashMap<String, Label>();

    private final CommandList commandList = new CommandList();
    private Command prev = commandList;

    /**
     * Initialize after constructed.
     *
     * @param file selenese script file.
     * @param name test-case name.
     * @param runner Runner instance.
     * @param baseURL effective base URL.
     * @return this.
     */
    public TestCase initialize(File file, String name, Runner runner, String baseURL) {
        this.file = file;
        this.basename = (file != null) ? FilenameUtils.getBaseName(file.getName()) : "nofile";
        this.name = name;
        this.runner = runner;
        this.baseURL = baseURL.replaceFirst("/+$", ""); // remove trailing "/".
        this.proc = new CustomCommandProcessor(baseURL, runner.getDriver());
        return this;
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Runner getRunner() {
        return runner;
    }

    /**
     * Get CustomCommandProcessor generated at initialize.
     *
     * @return CustomCommandProcessor.
     */
    public CustomCommandProcessor getProc() {
        return proc;
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
     * Set speed of command execution (= wait time per each command).
     *
     * @param speed wait time (ms).
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    /**
     * Get speed of command execution.
     *
     * @return wait time (ms).
     */
    public long getSpeed() {
        return speed;
    }

    /**
     * Run built-in command of WebDriverCommandProcessor.
     *
     * @param name command name.
     * @param args arguments.
     * @return result.
     */
    public Object doBuiltInCommand(String name, String... args) {
        try {
            return proc.execute(name, args);
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
     * Evaluate expression and return boolean result.
     *
     * @param expr expression string.
     * @return result.
     */
    public boolean isTrue(String expr) {
        // return Boolean.parseBoolean(proc.doCommand("getEval", new String[] { replaceVariables(expr) }));
        return Boolean.parseBoolean(proc.doCommand("getEval", new String[] { expr }));
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
    public Result execute(Selenese parent) {
        Command current = commandList.first();
        Result totalResult = SUCCESS;
        while (current != null) {
            Result result = doCommand(current);
            if (current.canUpdate()) {
                runner.takeScreenshotAll(basename, current.getIndex(), this);
                if (!result.isSuccess())
                    runner.takeScreenshotOnFail(basename, current.getIndex(), this);
            }
            totalResult = totalResult.update(result);
            if (totalResult.isAborted())
                break;
            current = current.next(this);
            if (speed > 0) {
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    // ignore it.
                }
            }
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
