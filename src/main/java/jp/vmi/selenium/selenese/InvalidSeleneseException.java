package jp.vmi.selenium.selenese;

/**
 * Exception for invalid selenese syntax.
 */
public class InvalidSeleneseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param e exception.
     */
    public InvalidSeleneseException(Exception e) {
        super(e);
    }

    /**
     * Constructor.
     *
     * @param message message.
     */
    public InvalidSeleneseException(String message) {
        super(message);
    }
}
