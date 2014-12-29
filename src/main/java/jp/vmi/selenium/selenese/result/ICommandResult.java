package jp.vmi.selenium.selenese.result;

/**
 * Command result interface.
 */
public interface ICommandResult {

    /**
     * Get result.
     *
     * @return result.
     */
    Result getResult();

    /**
     * Get end time.
     *
     * @return end time.
     */
    long getEndTime();
}
