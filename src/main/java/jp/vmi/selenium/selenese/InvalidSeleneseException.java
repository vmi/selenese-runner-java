package jp.vmi.selenium.selenese;

/**
 * Exception for invalid selenese syntax.
 */
public class InvalidSeleneseException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String filename;
    private final String name;

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(Throwable)}
     * </p>
     * @param cause the cause.
     */
    @Deprecated
    public InvalidSeleneseException(Throwable cause) {
        this(cause, null, null);
    }

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(String)}
     * </p>
     * @param message the detail message.
     */
    @Deprecated
    public InvalidSeleneseException(String message) {
        this(message, null, null);
    }

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(Throwable)}
     * </p>
     * @param cause the cause.
     * @param filename filename of test case or test suite. (or null)
     * @param name name of test case or test suite. (or null)
     */
    public InvalidSeleneseException(Throwable cause, String filename, String name) {
        super(cause);
        this.filename = filename;
        this.name = name;
    }

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(String)}
     * </p>
     * @param message the detail message.
     * @param filename filename of test case or test suite. (or null)
     * @param name name of test case or test suite. (or null)
     */
    public InvalidSeleneseException(String message, String filename, String name) {
        super(message);
        this.filename = filename;
        this.name = name;
    }

    /**
     * Get filename of test case or test suite.
     *
     * @return filename.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get name of test case or test suite.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }
}
