package jp.vmi.junit.result;

import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 * test target interface.
 */
public interface ITestTarget {

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
