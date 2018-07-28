package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.parser.TestCaseIterator;
import jp.vmi.selenium.selenese.parser.TestElementIteratorFactory;
import jp.vmi.selenium.selenese.parser.TestSuiteEntry;
import jp.vmi.selenium.selenese.parser.TestSuiteIterator;

/**
 * Parser of test-project (test-suite including test-suites) script.
 */
public final class TestProjectParser {

    /**
     * Parse test-suites script.
     *
     * @param suiteIter test-suite iterator.
     * @param commandFactory command factory.
     * @return Selenese instance.
     */
    public static Selenese parse(TestSuiteIterator suiteIter, ICommandFactory commandFactory) {
        TestProject testProject = Binder.newTestProject(suiteIter.getFilename(), suiteIter.getName());
        try {
            TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> caseIterFactory = suiteIter.getTestCaseIteratorFactory();
            for (TestSuiteEntry entry : suiteIter) {
                TestCaseIterator caseIter = caseIterFactory.getTestElementIterator(entry);
                Selenese selenese = TestSuiteParser.parse(caseIter, commandFactory);
                testProject.addSelenese(selenese);
            }
        } catch (InvalidSeleneseException e) {
            testProject.addSelenese(Binder.newErrorTestSuite(e.getFilename(), e));
        }
        return suiteIter.isDummy() ? testProject.pullOutFirstFromSeleneseList() : testProject;
    }
}
