package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.io.FilenameUtils;

import jp.vmi.html.result.IHtmlResultTestCase;
import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.command.BlockEnd;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.command.BlockStart;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.CommandResultList;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.PathUtils;
import jp.vmi.selenium.selenese.utils.StopWatch;

import static jp.vmi.selenium.selenese.command.BlockStart.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * test-case object for execution.
 * <p>
 * Supports Selenium IDE flow control plugin.
 * </p>
 * @see <a href="https://github.com/davehunt/selenium-ide-flowcontrol">A flow control plugin for Selenium IDE</a>
 */
public class TestCase implements Selenese, ITestCase, IHtmlResultTestCase {

    private SourceType sourceType = SourceType.SELENESE;
    private String filename = null;
    private String baseName = null;
    private String name = null;

    private String baseURL = null;

    private BlockStart currentBlockStart = NO_BLOCK_START;
    private final Deque<BlockStart> blockStack = new ArrayDeque<>();
    private final CommandList commandList = Binder.newCommandList();
    private final CommandResultList cresultList = new CommandResultList();

    private final StopWatch stopWatch = new StopWatch();
    private LogRecorder logRecorder = null;

    private Lifecycle lifecycle = Lifecycle.FINAL;

    /**
     * Initialize after constructed.
     *
     * @param sourceType test-case source type.
     * @param filename selenese script filename. (This base name is used for generating screenshot file)
     * @param name test-case name.
     * @param baseURL effective base URL.
     * @return this.
     */
    public TestCase initialize(SourceType sourceType, String filename, String name, String baseURL) {
        filename = PathUtils.normalize(filename);
        String baseName = filename != null ? FilenameUtils.getBaseName(filename) : name;
        if (name == null)
            name = baseName;
        this.sourceType = sourceType;
        this.filename = filename;
        this.baseName = baseName;
        this.name = name;
        this.baseURL = baseURL.replaceFirst("/+$", ""); // remove trailing "/".
        return this;
    }

    @Override
    public Type getType() {
        return Type.TEST_CASE;
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    @Override
    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    /**
     * Test-case source type.
     *
     * @return source type.
     */
    public SourceType getSourceType() {
        return sourceType;
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
    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getBaseName() {
        return baseName;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Get base URL in the test-case.
     *
     * @return base URL.
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Get command list.
     *
     * @return command list.
     */
    @Override
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

    @Override
    public void setLogRecorder(LogRecorder logRecorder) {
        if (this.logRecorder != null)
            throw new SeleneseRunnerRuntimeException("The log recorder of " + this + " is already set.");
        this.logRecorder = logRecorder;
    }

    @Override
    public LogRecorder getLogRecorder() {
        return logRecorder;
    }

    @Override
    public Result getResult() {
        return cresultList.getResult();
    }

    /**
     * Get test-case result list.
     *
     * @return test-case result list.
     */
    @Override
    public CommandResultList getResultList() {
        return cresultList;
    }

    /**
     * Add command to command list.
     *
     * @param command command.
     */
    public void addCommand(ICommand command) {
        command.setBlockStart(currentBlockStart);
        if (command instanceof BlockEnd) {
            currentBlockStart.setBlockEnd((BlockEnd) command);
            currentBlockStart = blockStack.pop();
        }
        if (command instanceof BlockStart) {
            blockStack.push(currentBlockStart);
            currentBlockStart = (BlockStart) command;
        }
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
        int i = commandList.size() + 1;
        ICommand command = commandFactory.newCommand(i, name, args);
        addCommand(command);
    }

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent, Context context) {
        if (commandList.isEmpty())
            return cresultList.setResult(SUCCESS);
        if (parent instanceof TestCase) {
            try {
                context.setCurrentTestCase(this);
                return commandList.execute(context, ((TestCase) parent).getResultList());
            } finally {
                context.setCurrentTestCase((TestCase) parent);
            }
        } else {
            context.setCurrentTestCase(this);
            context.resetState();
            cresultList.setEndTime(System.currentTimeMillis());
            return commandList.execute(context, cresultList);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TestCase[").append(name).append("]");
        if (filename != null)
            s.append(" (").append(filename).append(")");
        return s.toString();
    }
}
