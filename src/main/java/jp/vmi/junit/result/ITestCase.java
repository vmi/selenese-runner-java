package jp.vmi.junit.result;

import jp.vmi.selenium.selenese.utils.LogRecorder;

/**
 * test-case interface.
 */
public interface ITestCase extends ITestTarget {

    /**
     * Get log recorder.
     *
     * @return log recorder.
     */
    LogRecorder getLogRecorder();
}
