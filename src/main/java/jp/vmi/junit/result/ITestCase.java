package jp.vmi.junit.result;

import jp.vmi.selenium.selenese.utils.LogRecorder;

/**
 * test-case interface.
 */
public interface ITestCase extends ITestTarget {

    /**
     * Set log recorder.
     *
     * @param logRecorder log recorder.
     */
    void setLogRecorder(LogRecorder logRecorder);

    /**
     * Get log recorder.
     *
     * @return log recorder.
     */
    LogRecorder getLogRecorder();
}
