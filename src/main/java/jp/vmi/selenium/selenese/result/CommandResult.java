package jp.vmi.selenium.selenese.result;

import jp.vmi.selenium.selenese.command.ICommand;

/**
 * Pair of command and result.
 */
public class CommandResult {

    private final String sequence;
    private final ICommand command;
    private final Result result;
    private final long startTime; // ms
    private final long endTime; // ms

    /**
     * Constructor.
     *
     * @param sequence sequence.
     * @param command command.
     * @param result the result of above command.
     * @param startTime start time.
     * @param endTime end time.
     */
    public CommandResult(String sequence, ICommand command, Result result, long startTime, long endTime) {
        this.sequence = sequence;
        this.command = command;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Get sequence.
     *
     * @return sequenc.
     */
    public String getSequence() {
        return sequence;
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
