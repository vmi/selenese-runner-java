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
        super(message);
    }

    /**
     * Constructor.
     *
     * @param e Exception.
     */
    public Failure(Exception e) {
        super(e.getMessage());
    }

    public Failure(Result... results) {
        super(results);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isInterrupted() {
        return true;
    }

    @Override
    public boolean isFailed() {
        return true;
    }

    @Override
    public int exitCode() {
        return 3;
    }
}
