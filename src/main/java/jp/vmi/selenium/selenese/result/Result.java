package jp.vmi.selenium.selenese.result;

/**
 * Result of command execution.
 */
public abstract class Result {

    /** Default success */
    public static final Result SUCCESS = new Success("Success");

    private final String message;

    /**
     * Constructor.
     *
     * @param message result message.
     */
    public Result(String message) {
        this.message = message;
    }

    /**
     * Get result message.
     *
     * @return result message.
     */
    public String getMessage() {
        return message;
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
            return new Failure(this.getMessage() + "\n" + newResult.getMessage());
        else if (newResult.isFailed())
            return new Warning(this.getMessage() + "\n" + newResult.getMessage());
        else
            return this;
    }

    @Override
    public String toString() {
        return "[" + message + "]";
    }
}
