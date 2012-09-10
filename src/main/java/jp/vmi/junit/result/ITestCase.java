package jp.vmi.junit.result;

/**
 * test-case interface.
 */
public interface ITestCase {

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
}
