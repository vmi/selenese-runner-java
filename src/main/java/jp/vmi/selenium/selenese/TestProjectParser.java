package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.parser.TestCaseIterator;
import jp.vmi.selenium.selenese.parser.TestElementIteratorFactory;
import jp.vmi.selenium.selenese.parser.TestProjectReader;
import jp.vmi.selenium.selenese.parser.TestSuiteEntry;
import jp.vmi.selenium.selenese.parser.TestSuiteIterator;

/**
 * Parser of test-project (test-suite including test-suites) script.
 */
public final class TestProjectParser {

    /**
     * Parse test-project script.
     *
     * @param sourceType test-project source type.
     * @param projectReader test-project reader.
     * @param commandFactory command factory.
     * @return Selenese instance.
     * @throws InvalidSeleneseException invalid selenese exception.
     */
    public static Selenese parse(SourceType sourceType, TestProjectReader projectReader, ICommandFactory commandFactory) throws InvalidSeleneseException {
        TestSuiteIterator suiteIter = projectReader.getTestSuiteIterator();
        TestProject testProject = Binder.newTestProject(suiteIter.getFilename(), suiteIter.getName());
        testProject.setId(projectReader.getId());
        testProject.setTestCaseMap(projectReader.getTestCaseMap());
        try {
            TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> caseIterFactory = suiteIter.getTestCaseIteratorFactory();
            for (TestSuiteEntry entry : suiteIter) {
                TestCaseIterator caseIter = caseIterFactory.getTestElementIterator(entry);
                Selenese selenese = TestSuiteParser.parse(sourceType, caseIter, projectReader, commandFactory);
                testProject.addSelenese(selenese);
            }
        } catch (InvalidSeleneseException e) {
            testProject.addSelenese(Binder.newErrorTestSuite(e.getFilename(), e));
        }
        return suiteIter.isDummy() ? testProject.pullOutFirstFromSeleneseList() : testProject;
    }
}
