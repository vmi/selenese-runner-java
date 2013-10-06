package jp.vmi.selenium.selenese;

import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;

/**
 *
 */
public class ErrorTestCase extends ErrorSource implements ITestCase {

    private final LogRecorder logRecorder = new LogRecorder();

    @Override
    public LogRecorder getLogRecorder() {
        return logRecorder;
    }

    @Override
    public ErrorTestCase initialize(String filename, InvalidSeleneseException e) {
        return (ErrorTestCase) super.initialize(filename, e);
    }

    @Override
    public Type getType() {
        return Type.TEST_CASE;
    }

    @ExecuteTestCase
    @Override
    public Result execute(Selenese parent, Runner runner) throws InvalidSeleneseException {
        return super.execute(parent, runner);
    }

    /**
     * Get command list.
     * 
     * @return command list.
     */
    public CommandList getCommandList() {
        return new CommandList();
    }
}
