package jp.vmi.junit.result;

/**
 * test-suite interface.
 */
public interface ITestSuite {

    /**
     * Get test-suite name.
     *
     * @return test-suite name.
     */
    String getName();

    /**
     * Is error instance?
     *
     * @return true if this is error instance.
     */
    boolean isError();
}
