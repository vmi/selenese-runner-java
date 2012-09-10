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
    public Level getLevel() {
        return Level.WARNING;
    }
}
