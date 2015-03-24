package jp.vmi.selenium.selenese;

import jp.vmi.html.result.IHtmlResultTestCase;
import jp.vmi.junit.result.ITestCase;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.selenese.result.CommandResultList;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.utils.LogRecorder;

/**
 * Test-case with errrors.
 */
public class ErrorTestCase extends ErrorSource implements ITestCase, IHtmlResultTestCase {

    private LogRecorder logRecorder = null;

    @Override
    public void setLogRecorder(LogRecorder logRecorder) {
        this.logRecorder = logRecorder;
    }

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
    public Result execute(Selenese parent, Context context) throws InvalidSeleneseException {
        return super.execute(parent, context);
    }

    /**
     * Get command list.
     *
     * @return command list.
     */
    @Override
    public CommandList getCommandList() {
        return new CommandList();
    }

    /**
     * Get test-case result list.
     *
     * @return test-case result list.
     */
    @Override
    public CommandResultList getResultList() {
        return new CommandResultList();
    }
}
