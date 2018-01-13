package jp.vmi.selenium.selenese.parser;

import jp.vmi.selenium.selenese.InvalidSeleneseException;

/**
 * Test suite iterator with iterable.
 */
public interface TestCaseIterator extends TestElementIterator<TestCaseEntry> {

    /**
     * Get command iterator factory.
     *
     * @return command iterator factory.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    TestElementIteratorFactory<CommandIterator, TestCaseEntry> getCommandIteratorFactory() throws InvalidSeleneseException;
}
