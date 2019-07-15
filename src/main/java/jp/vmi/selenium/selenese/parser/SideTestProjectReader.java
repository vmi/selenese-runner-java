package jp.vmi.selenium.selenese.parser;

import java.io.InputStream;

import jp.vmi.selenium.runner.model.side.SideFile;
import jp.vmi.selenium.runner.model.side.SideProject;
import jp.vmi.selenium.selenese.InvalidSeleneseException;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.TestCaseMap;
import jp.vmi.selenium.selenese.TestCaseParser;
import jp.vmi.selenium.selenese.command.ICommandFactory;

/**
 * Test project of SideFile format.
 */
public class SideTestProjectReader implements TestProjectReader {

    /**
     * Create new instance of SideTestProjectReader.
     *
     * @param filename side script file. (Don't use to open a file. It is used as a label and is used to generate filenames based on it)
     * @param is input stream of *.side file.
     * @return test-project.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public static SideTestProjectReader newInstance(String filename, InputStream is) throws InvalidSeleneseException {
        SideProject side = SideFile.parse(filename, is);
        return new SideTestProjectReader(side);
    }

    private final SideProject side;
    private final TestCaseMap testCaseMap = new TestCaseMap();

    private SideTestProjectReader(SideProject side) {
        this.side = side;
    }

    @Override
    public TestSuiteIterator getTestSuiteIterator() throws InvalidSeleneseException {
        return new SideTestSuiteIterator(side);
    }

    @Override
    public void setupTestCaseMap(SourceType sourceType, ICommandFactory commandFactory) {
        side.getTestMap().keySet().forEach(id -> {
            SideCommandIterator iter = new SideCommandIterator(side, id);
            TestCaseParser.parse(sourceType, iter, this, commandFactory);
        });
    }

    @Override
    public String getId() {
        return side.getId();
    }

    @Override
    public TestCaseMap getTestCaseMap() {
        return testCaseMap;
    }
}
