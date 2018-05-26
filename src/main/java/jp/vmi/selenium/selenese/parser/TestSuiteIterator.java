package jp.vmi.selenium.selenese.parser;

import jp.vmi.selenium.selenese.InvalidSeleneseException;

/**
 * Test suite iterator with iterable.
 */
public interface TestSuiteIterator extends TestElementIterator<TestSuiteEntry> {

    /**
     * Get test case iterator factory.
     *
     * @return test case iterator factory.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> getTestCaseIteratorFactory() throws InvalidSeleneseException;
}
