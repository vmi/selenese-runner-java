package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.result.Result;

/**
 * Selenese script interface.
 */
public interface Selenese {

    /**
     * Get script name.
     *
     * @return script name.
     */
    String getName();

    /**
     * Execute script.
     *
     * @param runner Runner object.
     * @return result.
     */
    Result execute(Runner runner);
}
