package jp.vmi.selenium.selenese.parser;

import java.util.NoSuchElementException;

import jp.vmi.selenium.selenese.InvalidSeleneseException;

/**
 * Selenese test suite iterator.
 */
public class SeleneseTestSuiteIterator extends AbstractTestElementIterator<TestSuiteEntry> implements TestSuiteIterator {

    private TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> iterFactory;
    private boolean hasNext = true;

    private static class SingleTestCaseIterator extends AbstractTestElementIterator<TestCaseEntry> implements TestCaseIterator {

        private final CommandIterator commandIterator;
        private boolean hasNext = true;

        public SingleTestCaseIterator(CommandIterator commandIterator) {
            super(commandIterator.getFilename());
            setName(commandIterator.getName());
            setId(commandIterator.getId());
            this.commandIterator = commandIterator;
        }

        @Override
        public boolean isDummy() {
            return true;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public TestCaseEntry next() {
            if (!hasNext)
                throw new NoSuchElementException();
            hasNext = false;
            return new TestCaseEntry(false, commandIterator.getId(), commandIterator.getName());
        }

        @Override
        public TestElementIteratorFactory<CommandIterator, TestCaseEntry> getCommandIteratorFactory() {
            return testCaseEntry -> commandIterator;
        }

    }

    /**
     * Constructor.
     *
     * @param iter TestCase or TestSuite.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    SeleneseTestSuiteIterator(TestElementIterator<?> iter) throws InvalidSeleneseException {
        super(iter.getFilename());
        setName(ParserUtils.getNameFromFilename(iter.getFilename()));
        setId(iter.getFilename());
        if (iter instanceof SeleneseTestCaseIterator)
            iterFactory = suiteEntry -> (SeleneseTestCaseIterator) iter;
        else // if SeleneseCommandIterator
            iterFactory = suiteEntry -> new SingleTestCaseIterator((CommandIterator) iter);
    }

    @Override
    public boolean isDummy() {
        return true;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public TestSuiteEntry next() {
        if (!hasNext)
            throw new NoSuchElementException();
        hasNext = false;
        return new TestSuiteEntry(getId(), getName());
    }

    @Override
    public TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> getTestCaseIteratorFactory() {
        return iterFactory;
    }
}
