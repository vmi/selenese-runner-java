package jp.vmi.junit.result;

import jp.vmi.selenium.selenese.utils.StopWatch;

/**
 * Test target interface.
 */
public interface ITestTarget {

    /**
     * Selenese lifecycle types.
     */
    @SuppressWarnings("javadoc")
    public enum Lifecycle {
        FINAL, DRAFT
    }

    /**
     * Flag for triggering LIFECYCLE_DRAFT in a selenese test.
     */
    public static final String FLAG_LIFECYCLE_DRAFT = "### lifecycle=" + Lifecycle.DRAFT.name() + " ###";

    /**
     * Get test-target file base name.
     *
     * For example, return "def" when file name is "/abc/def.html".
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

    /**
     * Get the lifecycle type.
     *
     * @return lifecycle type.
     */
    Lifecycle getLifecycle();

    /**
     * Set the lifecycle type.
     *
     * @param lifecycle
     */
    void setLifecycle(Lifecycle lifecycle);
}
