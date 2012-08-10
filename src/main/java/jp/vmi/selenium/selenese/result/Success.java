package jp.vmi.selenium.selenese.result;

/**
 * Result of success.
 */
public class Success extends Result {

    /** Default success */
    public static final Success SUCCESS = new Success("");

    /**
     * Constructor.
     *
     * @param message success message.
     */
    public Success(String message) {
        super(message);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return false;
    }

    @Override
    public int exitCode() {
        return 0;
    }
}
