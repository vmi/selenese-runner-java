package jp.vmi.selenium.selenese.result;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Result of command execution.
 */
public abstract class Result {

    private final String message;

    private final List<String> errorLogs = new LinkedList<String>();

    private final List<String> normalLogs = new LinkedList<String>();

    /**
     * Constructor.
     *
     * @param message result message.
     */
    public Result(String message) {
        this.message = message;
    }

    /**
     * Constructor.
     *
     * @param result object.
     */
    public Result(Result... results) {
        //message
        List<String> messages = new LinkedList<String>();
        for (Result result : results) {
            messages.add(result.getMessage());
        }
        this.message = StringUtils.join(messages, "\n");

        //logs
        for (Result result : results) {
            this.getErrorLogs().addAll(result.getErrorLogs());
            this.getNormalLogs().addAll(result.getNormalLogs());
        }
    }

    /**
     * Get result message.
     *
     * @return result message.
     */
    public String getMessage() {
        return message;
    }

    public List<String> getErrorLogs() {
        return errorLogs;
    }

    public List<String> getNormalLogs() {
        return normalLogs;
    }

    public void addErrorLog(String log) {
        errorLogs.add(log);
        normalLogs.add(log);
    }

    public void addNormalLog(String log) {
        normalLogs.add(log);
    }

    /**
     * Is result of success?
     *
     * @return true if this is Success.
     */
    public abstract boolean isSuccess();

    /**
     * Is command interrupted?
     *
     * @return true if command is interrupted.
     */
    public abstract boolean isInterrupted();

    /**
     * Is command failed?
     *
     * @return true if command is failed.
     */
    public abstract boolean isFailed();

    /**
     * exit code for terminating Selenese Runner.
     *
     * @return exit code.
     */
    public abstract int exitCode();

    /**
     * Update total result.
     *
     * @param newResult new result.
     * @return updated total result.
     */
    public Result update(Result newResult) {
        if (newResult.isInterrupted())
            return new Failure(this, newResult);
        else if (newResult.isFailed())
            return new Warning(this, newResult);
        else
            return this;
    }

    @Override
    public String toString() {
        return "[" + message + "]";
    }
}
