package jp.vmi.selenium.selenese.result;

/**
 * Result of failure.
 */
public class Failure extends Result {

    /**
     * Constructor.
     *
     * @param message failure message.
     */
    public Failure(String message) {
        super("Failure: " + message);
    }

    /**
     * Constructor.
     *
     * @param e Exception.
     */
    public Failure(Exception e) {
        super(e.getMessage());
    }

    @Override
    public Level getLevel() {
        return Level.FAILURE;
    }
}
