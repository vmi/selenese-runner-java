package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.inject.DoCommand;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.StopWatch;

import static jp.vmi.selenium.selenese.result.Success.*;
import static jp.vmi.selenium.selenese.result.Unexecuted.*;

/**
 * test-case object for execution.
 * <p>
 * Supports Selenium IDE flow control plugin.
 * </p>
 * @see <a href="https://github.com/davehunt/selenium-ide-flowcontrol">A flow control plugin for Selenium IDE</a>
 */
public class TestCase implements Selenese, ITestCase {

    private String filename = null;
    private String baseName = "nofile";
    private String name = null;
    private String baseURL = null;

    private SeleneseRunnerCommandProcessor proc = null;

    private final Map<String, Deque<String>> collectionMap = new HashMap<String, Deque<String>>();
    private final Map<String, Label> labelCommandMap = new HashMap<String, Label>();

    private final CommandList commandList = new CommandList();

    private final StopWatch stopWatch = new StopWatch();
    private final LogRecorder logRecorder = new LogRecorder();
    private Result result = UNEXECUTED;

    /**
     * Initialize after constructed.
     *
     * @param filename selenese script filename. (This base name is used for generating screenshot file)
     * @param name test-case name.
     * @param runner Runner instance.
     * @param baseURL effective base URL.
     * @return this.
     */
    public TestCase initialize(String filename, String name, Runner runner, String baseURL) {
        this.filename = filename;
        if (filename != null)
            this.baseName = FilenameUtils.getBaseName(filename);
        this.name = name;
        this.baseURL = baseURL.replaceFirst("/+$", ""); // remove trailing "/".
        this.proc = new SeleneseRunnerCommandProcessor(runner, runner.getDriver(), runner.getVarsMap());
        return this;
    }

    @Override
    public Type getType() {
        return Type.TEST_CASE;
    }

    @Override
    public boolean isError() {
        return false;
    }

    /**
     * Get filename of test-case.
     *
     * @return filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get base name for screenshot file name.
     *
     * @return base name
     */
    public String getBaseName() {
        return baseName;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get CustomCommandProcessor generated at initialize.
     *
     * @return CustomCommandProcessor.
     */
    public SeleneseRunnerCommandProcessor getProc() {
        return proc;
    }

    /**
     * Get command list.
     * 
     * @return command list.
     */
    public CommandList getCommandList() {
        return commandList;
    }

    /**
     * Get stop watch.
     *
     * @return stop watch.
     */
    @Override
    public StopWatch getStopWatch() {
        return stopWatch;
    }

    /**
     * Get log recorder.
     *
     * @return log recorder.
     */
    @Override
    public LogRecorder getLogRecorder() {
        return logRecorder;
    }

    /**
     * Get test-case result.
     *
     * @return test-case result.
     */
    public Result getResult() {
        return result;
    }

    /**
     * Evaluate expression and return boolean result.
     *
     * @param expr expression string.
     * @return result.
     */
    public boolean isTrue(String expr) {
        return (Boolean) proc.execute("getEval", new String[] { expr });
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
        commandList.add(command);
    }

    @DoCommand
    protected Result doCommand(Command command, Runner runner) {
        try {
            return command.doCommand(this, runner);
        } catch (Exception e) {
            return new Error(e);
        }
    }

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent, Runner runner) {
        if (commandList.isEmpty())
            return result = SUCCESS;
        logRecorder.setPrintStream(runner.getPrintStream());
        runner.setDefaultBaseURL(baseURL);
        Command command = commandList.first();
        while (command != null) {
            Result r = doCommand(command, runner);
            result = result.update(r);
            if (result.isAborted())
                break;
            command = command.next(this, runner);
            runner.waitSpeed();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TestCase[").append(name).append("]");
        if (filename != null)
            s.append(" (").append(filename).append(")");
        return s.toString();
    }
}
