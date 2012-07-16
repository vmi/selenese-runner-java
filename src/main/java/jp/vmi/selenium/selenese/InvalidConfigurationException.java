package jp.vmi.selenium.selenese;

public class InvalidConfigurationException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }
}
