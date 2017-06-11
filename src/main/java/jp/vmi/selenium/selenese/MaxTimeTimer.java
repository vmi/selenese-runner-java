package jp.vmi.selenium.selenese;

/**
 * Interface of timer for --max-time option.
 */
public interface MaxTimeTimer {

    /**
     * Start timer.
     */
    default void start() {
        // no operation.
    }

    /**
     * Stop timer.
     */
    default void stop() {
        // no operation.
    }
}
