package jp.vmi.selenium.selenese.result;

/**
 * Result of error.
 *
 * This result indicates that the command has ended abnormally.
 */
public class Error extends Result {

    /**
     * Constructor.
     *
     * @param message error message.
     */
    public Error(String message) {
        super("Error", message);
    }

    /**
     * Constructor.
     *
     * @param e Exception.
     */
    public Error(Exception e) {
        super("Error", e);
    }

    /**
     * Constructor.
     *
     * @param message error message.
     * @param e Exception.
     */
    public Error(String message, Exception e) {
        super("Error", message, e);
    }

    /**
     * Constructor.
     *
     * @param childResult child result.
     */
    public Error(Result childResult) {
        super(childResult);
    }

    @Override
    public Level getLevel() {
        return Level.ERROR;
    }
}
