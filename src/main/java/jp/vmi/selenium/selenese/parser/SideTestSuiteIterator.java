package jp.vmi.selenium.selenese.parser;

import java.io.InputStream;
import java.util.Iterator;

import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.side.Side;
import jp.vmi.selenium.selenese.side.SideSuite;

/**
 * Iterator and iterable of test suite of SideFile format.
 */
public class SideTestSuiteIterator extends AbstractTestElementIterator<TestSuiteEntry> implements TestSuiteIterator {

    private final Side side;
    private final Iterator<SideSuite> iter;

    /**
     * Constructor.
     *
     * @param filename side script file. (Don't use to open a file. It is used as a label and is used to generate filenames based on it)
     * @param is input stream of *.side file.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public SideTestSuiteIterator(String filename, InputStream is) throws InvalidSeleneseException {
        super(filename);
        this.side = Side.parse(filename, is);
        setName(this.side.getName());
        setId(this.side.getId());
        this.iter = side.getSuites().iterator();
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public TestSuiteEntry next() {
        SideSuite suite = iter.next();
        return new TestSuiteEntry(suite.getId(), suite.getName());
    }

    @Override
    public TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> getTestCaseIteratorFactory() {
        return suiteEntry -> new SideTestCaseIterator(side, suiteEntry.id);
    }
}
