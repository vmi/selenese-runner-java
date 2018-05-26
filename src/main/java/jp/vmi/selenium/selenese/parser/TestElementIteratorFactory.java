package jp.vmi.selenium.selenese.parser;

import jp.vmi.selenium.selenese.InvalidSeleneseException;

/**
 * Test case iterator factory.
 *
 * @param <I> type of test element iterator.
 * @param <E> type of test element entry.
 */
public interface TestElementIteratorFactory<I extends TestElementIterator<?>, E extends TestElementEntry> {

    /**
     * Get test case iterator.
     *
     * @param entry test suite entry.
     * @throws InvalidSeleneseException invalid selenese exception.
     *
     * @return test case iterator.
     */
    I getTestElementIterator(E entry) throws InvalidSeleneseException;
}
