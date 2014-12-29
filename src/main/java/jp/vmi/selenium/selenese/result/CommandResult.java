package jp.vmi.selenium.selenese.result;

import jp.vmi.selenium.selenese.command.ICommand;

/**
 * Pair of command and result.
 */
public class CommandResult implements ICommandResult {

    private final ICommand command;
    private final Result result;
    private final long startTime; // ms
    private final long endTime; // ms

    /**
     * Constructor.
     *
     * @param command command.
     * @param result the result of above command.
     * @param startTime start time.
     * @param endTime end time.
     */
    public CommandResult(ICommand command, Result result, long startTime, long endTime) {
        this.command = command;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Get command.
     *
     * @return command.
     */
    public ICommand getCommand() {
        return command;
    }

    /**
     * Get result.
     *
     * @return result.
     */
    @Override
    public Result getResult() {
        return result;
    }

    /**
     * Get start time.
     *
     * @return start time. (ms)
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get end time.
     *
     * @return end time. (ms)
     */
    @Override
    public long getEndTime() {
        return endTime;
    }

    /**
     * Get duration.
     *
     * @return duration. (ms)
     */
    public long getDuration() {
        return endTime - startTime;
    }
}
