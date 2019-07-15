package jp.vmi.selenium.selenese.parser;

import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestCaseMap;
import jp.vmi.selenium.selenese.command.ICommandFactory;

/**
 * Test project reader.
 */
public interface TestProjectReader {

    /**
     * Get test-suite iterator.
     *
     * @return test-suite iterator.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    TestSuiteIterator getTestSuiteIterator() throws InvalidSeleneseException;

    /**
     * Setup test-case map.
     *
     * @param sourceType source type.
     * @param commandFactory command factory.
     */
    default void setupTestCaseMap(SourceType sourceType, ICommandFactory commandFactory) {
        // no operation.
    }

    /**
     * Get test-project id.
     *
     * @return test-project id.
     */
    String getId();

    /**
     * Get test-case map.
     *
     * @return test-case map.
     */
    TestCaseMap getTestCaseMap();

    /**
     * Get parsed test-case.
     *
     * @param id test-case id.
     * @return test-case or null.
     */
    default TestCase getTestCaseById(String id) {
        return getTestCaseMap().getById(id);
    }

    /**
     * Add test-case.
     *
     * @param testCase test-case.
     */
    default void addTestCase(TestCase testCase) {
        getTestCaseMap().put(testCase);
    }
}
