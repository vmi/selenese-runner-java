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
     * Get Runner instance.
     * 
     * @return Runner instance.
     */
    Runner getRunner();

    /**
     * Execute script.
     *
     * @param parent parent selenese instance or null.
     * @exception InvalidSeleneseException failed in parsing or executing selenese.
     * @return result.
     */
    Result execute(Selenese parent) throws InvalidSeleneseException;
}
