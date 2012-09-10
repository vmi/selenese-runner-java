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
    public Level getLevel() {
        return Level.ERROR;
    }
}
