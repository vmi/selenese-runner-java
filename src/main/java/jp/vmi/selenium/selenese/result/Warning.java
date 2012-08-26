package jp.vmi.selenium.selenese.result;

/**
 * Result of warning.
 */
public class Warning extends Result {

    /**
     * Constructor.
     *
     * @param message warning message.
     */
    public Warning(String message) {
        super(message);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public boolean isFailed() {
        return true;
    }

    @Override
    public int exitCode() {
        return 2;
    }
}
