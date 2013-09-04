package jp.vmi.selenium.selenese.result;

/**
 * Result of command execution.
 */
public abstract class Result {

    /**
     * Result Level.
     */
    @SuppressWarnings("javadoc")
    public static enum Level {
        UNEXECUTED(-1, 0),
        SUCCESS(0, 0),
        WARNING(1, 0),
        FAILURE(2, 3),
        ERROR(3, 3);

        public final int value;
        public final int exitCode;

        private Level(int value, int exitCode) {
            this.value = value;
            this.exitCode = exitCode;
        }
    }

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
     * Get result level.
     *
     * @return result level.
     */
    public abstract Level getLevel();

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
    public boolean isSuccess() {
        return getLevel() == Level.SUCCESS;
    }

    /**
     * Is command interrupted?
     *
     * @return true if command is interrupted.
     */
    public boolean isAborted() {
        return getLevel().value >= Level.FAILURE.value;
    }

    /**
     * Is command failed?
     *
     * @return true if command is failed.
     */
    public boolean isFailed() {
        return getLevel().value >= Level.WARNING.value;
    }

    /**
     * Update total result.
     *
     * @param newResult new result.
     * @return updated total result.
     */
    public Result update(Result newResult) {
        return newResult.getLevel().value > this.getLevel().value ? newResult : this;
    }

    @Override
    public String toString() {
        return "[" + message + "]";
    }
}
