package jp.vmi.selenium.selenese;

import org.openqa.selenium.WebDriverException;

/**
 * The exception which occurs when a selenese command has caused an error.
 */
public class SeleneseRunnerRuntimeException extends WebDriverException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(Throwable)}
     * </p>
     * @param cause the cause.
     */
    public SeleneseRunnerRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(String)}
     * </p>
     * @param message error message.
     */
    public SeleneseRunnerRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * <p>
     * {@link Throwable#Throwable(String, Throwable)}
     * </p>
     * @param message error message.
     * @param cause the cause.
     */
    public SeleneseRunnerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
