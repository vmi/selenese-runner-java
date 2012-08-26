package jp.vmi.selenium.selenese.result;

/**
 * Result of error.
 */
public class Error extends Result {

    /**
     * Constructor.
     *
     * @param message error message.
     */
    public Error(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param e Exception.
     */
    public Error(Exception e) {
        super(e.getMessage());
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
