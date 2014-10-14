package jp.vmi.junit.result;

import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 * Test target interface.
 */
public interface ITestTarget {

    /**
     * Get test-case file base name.
     *
     * @return test-case file base name.
     */
    String getBaseName();

    /**
     * Get test-case name.
     *
     * @return test-case name.
     */
    String getName();

    /**
     * Is error instance?
     *
     * @return true if this is error instance.
     */
    boolean isError();

    /**
     * Get stop watch.
     *
     * @return stop watch.
     */
    StopWatch getStopWatch();
}
