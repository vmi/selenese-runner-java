package jp.vmi.selenium.runner.model;

import java.util.List;
import java.util.Map;

/**
 * Test project.
 *
 * @param <S> type of test suite.
 * @param <T> type of test case.
 * @param <C> type of test command.
 */
public interface IProject<S extends ISuite<T, C>, T extends ITest<C>, C extends ICommand> extends Id {

    /**
     * Get filename.
     *
     * @return filename.
     */
    String getFilename();

    /**
     * Get name.
     *
     * @return name.
     */
    String getName();

    /**
     * Get base URL.
     *
     * @return base URL.
     */
    String getUrl();

    /**
     * Get list of test suites.
     *
     * @return list of test suites.
     */
    List<S> getSuites();

    /**
     * Get map of test suites.
     *
     * @return map of test suites.
     */
    Map<String, S> getSuiteMap();

    /**
     * Get map of test cases.
     *
     * @return map of test cases.
     */
    Map<String, T> getTestMap();
}
