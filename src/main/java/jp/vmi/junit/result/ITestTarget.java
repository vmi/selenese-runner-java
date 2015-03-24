package jp.vmi.junit.result;

import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 * Test target interface.
 */
public interface ITestTarget {

    /**
     * Get test-target file base name.
     *
     * @return test-target file base name.
     */
    String getBaseName();

    /**
     * Get test-target name.
     *
     * @return test-target name.
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
