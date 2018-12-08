package jp.vmi.selenium.selenese.parser;

import java.util.Iterator;

import jp.vmi.selenium.runner.model.side.SideProject;
import jp.vmi.selenium.runner.model.side.SideSuite;
import jp.vmi.selenium.runner.model.side.SideTest;

/**
 * Iterator and iterable of test suite of SideFile format.
 */
public class SideTestCaseIterator extends AbstractTestElementIterator<TestCaseEntry> implements TestCaseIterator {

    private final SideProject side;
    private final Iterator<SideTest> iter;

    /**
     * Constructor.
     *
     * @param side side format data.
     * @param testSuiteId test suite id.
     */
    public SideTestCaseIterator(SideProject side, String testSuiteId) {
        super(side.getFilename());
        this.side = side;
        SideSuite suite = side.getSuiteMap().get(testSuiteId);
        setName(suite.getName());
        setId(suite.getId());
        this.iter = suite.getTests().iterator();
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public TestCaseEntry next() {
        SideTest test = iter.next();
        return new TestCaseEntry(true, test.getId(), test.getName());
    }

    @Override
    public TestElementIteratorFactory<CommandIterator, TestCaseEntry> getCommandIteratorFactory() {
        return caseEntry -> new SideCommandIterator(side, caseEntry.id);
    }
}
