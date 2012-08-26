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
     * @param parent parent selenese instance or null.
     * @param runner Runner instance.
     * @exception InvalidSeleneseException failed in parsing or executing selenese.
     * @return result.
     */
    Result execute(Selenese parent, Runner runner) throws InvalidSeleneseException;
}
