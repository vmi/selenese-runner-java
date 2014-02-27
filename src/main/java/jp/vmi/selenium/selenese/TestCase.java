package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.inject.DoCommand;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;
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

    private final Map<String, Deque<String>> collectionMap = new HashMap<String, Deque<String>>();
    private final Map<String, Label> labelCommandMap = new HashMap<String, Label>();

    private final CommandList commandList = new CommandList();

    private final StopWatch stopWatch = new StopWatch();
    private final LogRecorder logRecorder = new LogRecorder();
    private Result result = UNEXECUTED;

    @Deprecated
    private Context context = null;

    /**
     * Initialize after constructed.
     *
     * @param filename selenese script filename. (This base name is used for generating screenshot file)
     * @param name test-case name.
     * @param runner Runner instance.
     * @param baseURL effective base URL.
     * @return this.
     */
    @Deprecated
    public TestCase initialize(String filename, String name, Runner runner, String baseURL) {
        TestCase testCase = initialize(filename, name, baseURL);
        testCase.setContext(runner);
        return testCase;
    }

    /**
     * Initialize after constructed.
     *
     * @param filename selenese script filename. (This base name is used for generating screenshot file)
     * @param name test-case name.
     * @param baseURL effective base URL.
     * @return this.
     */
    public TestCase initialize(String filename, String name, String baseURL) {
        this.filename = filename;
        if (filename != null)
            this.baseName = FilenameUtils.getBaseName(filename);
        this.name = name;
        this.baseURL = baseURL.replaceFirst("/+$", ""); // remove trailing "/".
        return this;
    }

    /**
     * Set Selenese Runner context for backward compatibility.
     *
     * @param context Selenese Runner context.
     */
    @Deprecated
    public void setContext(Context context) {
        this.context = context;
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
     * Set SubCommandMap instance for backward compatibility.
     * 
     * @param proc SubCommandMap intance. 
     */
    @Deprecated
    public void setProc(SubCommandMap proc) {
        this.context = proc.getContext();
    }

    /**
     * Get SubCommandMap instance generated at initialize.
     *
     * @return SubCommandMap instance.
     */
    @Deprecated
    public SubCommandMap getProc() {
        return context.getSubCommandMap();
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

    /**
     * Add command to command list.
     * 
     * @param commandFactory command factory.
     * @param name command name.
     * @param args command arguments.
     */
    public void addCommand(ICommandFactory commandFactory, String name, String... args) {
        int i = commandList.size();
        Command command = commandFactory.newCommand(i, name, args);
        addCommand(command);
    }

    @DoCommand
    protected Result doCommand(Command command, Context context) {
        try {
            return command.doCommand(this, (Runner) context);
        } catch (Exception e) {
            return new Error(e);
        }
    }

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent, Context context) {
        if (commandList.isEmpty())
            return result = SUCCESS;
        logRecorder.setPrintStream(context.getPrintStream());
        context.setDefaultBaseURL(baseURL);
        Command command = commandList.first();
        while (command != null) {
            Result r = doCommand(command, context);
            result = result.update(r);
            if (result.isAborted())
                break;
            command = command.next(this, (Runner) context);
            context.waitSpeed();
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
