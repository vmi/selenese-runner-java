package jp.vmi.selenium.selenese.result;

/**
 * Result of success.
 */
public class Success extends Result {

    /**
     * Constructor.
     *
     * @param message success message.
     */
    public Success(String message) {
        super(message);
    }

    /**
     * Constructor.
     */
    public Success() {
        this("Success");
    }

    @Override
    public Level getLevel() {
        return Level.SUCCESS;
    }
}
