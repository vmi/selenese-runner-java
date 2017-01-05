package jp.vmi.selenium.selenese;

import org.openqa.selenium.WebDriverException;

import jp.vmi.selenium.selenese.result.Error;

/**
 * The exception which occurs when a selenese command has caused an error.
 */
public class SeleneseCommandErrorException extends WebDriverException {

    private static final long serialVersionUID = 1L;

    private final Error error;

    /**
     * Constructor.
     *
     * @param message error message.
     */
    public SeleneseCommandErrorException(String message) {
        super(message);
        this.error = new Error(message);
    }

    /**
     * Constructor.
     *
     * @param error error object.
     */
    public SeleneseCommandErrorException(Error error) {
        super(error.getMessage());
        this.error = error;
    }

    /**
     * Get error result.
     *
     * @return error result.
     */
    public Error getError() {
        return error;
    }
}
