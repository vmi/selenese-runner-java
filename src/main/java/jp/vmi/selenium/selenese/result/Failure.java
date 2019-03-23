package jp.vmi.selenium.selenese.result;

/**
 * Result of failure.
 *
 * This result indicates that test-case itself broke.
 */
public class Failure extends Result {

    /**
     * Constructor.
     *
     * @param message failure message.
     */
    public Failure(String message) {
        super("Failure", message);
    }

    /**
     * Constructor.
     *
     * @param e Exception.
     */
    public Failure(Exception e) {
        super("Failure", e);
    }

    /**
     * Constructor.
     *
     * @param childResult child result.
     */
    public Failure(Result childResult) {
        super(childResult);
    }

    @Override
    public Level getLevel() {
        return Level.FAILURE;
    }
}
