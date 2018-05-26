package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.parser.TestCaseIterator;
import jp.vmi.selenium.selenese.parser.TestElementIteratorFactory;
import jp.vmi.selenium.selenese.parser.TestSuiteEntry;
import jp.vmi.selenium.selenese.parser.TestSuiteIterator;

/**
 * Parser of test-suites script.
 */
public final class TestSuitesParser {

    /**
     * Parse test-suites script.
     *
     * @param suiteIter test-suite iterator.
     * @param commandFactory command factory.
     * @return Selenese instance.
     */
    public static Selenese parse(TestSuiteIterator suiteIter, ICommandFactory commandFactory) {
        TestSuite testSuites = Binder.newTestSuite(suiteIter.getFilename(), suiteIter.getName());
        try {
            TestElementIteratorFactory<TestCaseIterator, TestSuiteEntry> caseIterFactory = suiteIter.getTestCaseIteratorFactory();
            for (TestSuiteEntry entry : suiteIter) {
                TestCaseIterator caseIter = caseIterFactory.getTestElementIterator(entry);
                Selenese selenese = TestSuiteParser.parse(caseIter, commandFactory);
                testSuites.addSelenese(selenese);
            }
        } catch (InvalidSeleneseException e) {
            testSuites.addSelenese(Binder.newErrorTestSuite(e.getFilename(), e));
        }
        return suiteIter.isDummy() ? testSuites.getSeleneseList().get(0) : testSuites;
    }
}
