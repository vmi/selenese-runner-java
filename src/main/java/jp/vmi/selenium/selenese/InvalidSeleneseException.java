package jp.vmi.selenium.selenese;

/**
 * Exception for invalid selenese syntax.
 */
public class InvalidSeleneseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(Throwable)}
     * </p>
     * @param cause the cause.
     */
    public InvalidSeleneseException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(String)}
     * </p>
     * @param message the detail message. 
     */
    public InvalidSeleneseException(String message) {
        super(message);
    }
}
