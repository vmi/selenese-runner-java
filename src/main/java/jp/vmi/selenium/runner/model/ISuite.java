package jp.vmi.selenium.runner.model;

import java.util.List;

/**
 * Test suite.
 *
 * @param <T> type of test case.
 * @param <C> type of test command.
 */
public interface ISuite<T extends ITest<C>, C extends ICommand> extends Id {

    /**
     * Get list of test cases.
     *
     * @return list of test cases
     */
    List<T> getTests();

    /**
     * Get parallel flag.
     *
     * @return parallel if true
     */
    boolean isParallel();

    /**
     * Get timeout.
     *
     * @return timeout
     */
    int getTimeout();

    /**
     * Get persist session flag.
     *
     * @return persist session if true
     */
    boolean isPersistSession();
}
