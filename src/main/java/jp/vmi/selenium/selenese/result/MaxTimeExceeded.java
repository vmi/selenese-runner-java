package jp.vmi.selenium.selenese.result;

/**
 * Result of Maximum execution time exceeded.
 * @since 2.9.1
 */
public class MaxTimeExceeded extends Result {

    /**
     * Default constructor.
     */
    public MaxTimeExceeded() {
        super("Maximum execution time exceeded");
    }

    /**
     * Constructor.
     *
     * @param message the detail message.
     */
    public MaxTimeExceeded(String message) {
        super("Maximum execution time exceeded", message);
    }

    /**
     * Constructor.
     *
     * @param cause the cause.
     */
    public MaxTimeExceeded(Exception cause) {
        super("Maximum execution time exceeded", cause);
    }

    /**
     * Constructor.
     *
     * @param childResult child result.
     */
    public MaxTimeExceeded(Result childResult) {
        super(childResult);
    }

    @Override
    public Level getLevel() {
        return Level.MAX_TIME_EXCEEDED;
    }
}
