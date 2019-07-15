package jp.vmi.selenium.selenese.parser;

import java.io.InputStream;

import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.TestCaseMap;

/**
 * Test project of Selenese format.
 */
public class SeleneseTestProjectReader implements TestProjectReader {

    /**
     * Create new instance of SeleneseTestProjectReader.
     *
     * @param filename filename of Selense. (label)
     * @param is input stream of script file. (test-case or test-suite)
     * @return test-project.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public static SeleneseTestProjectReader newInstance(String filename, InputStream is) throws InvalidSeleneseException {
        return new SeleneseTestProjectReader(filename, is);
    }

    private final SeleneseIteratorFactory factory;
    private final TestCaseMap testCaseMap = new TestCaseMap();

    private SeleneseTestProjectReader(String filename, InputStream is) throws InvalidSeleneseException {
        this.factory = SeleneseIteratorFactory.newInstance(filename, is);
    }

    @Override
    public TestSuiteIterator getTestSuiteIterator() throws InvalidSeleneseException {
        TestElementIterator<?> iter = factory.newIterator();
        return new SeleneseTestSuiteIterator(iter);
    }

    @Override
    public String getId() {
        return factory.getFilename();
    }

    @Override
    public TestCaseMap getTestCaseMap() {
        return testCaseMap;
    }
}
