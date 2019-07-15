package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.parser.CommandIterator;
import jp.vmi.selenium.selenese.parser.TestCaseEntry;
import jp.vmi.selenium.selenese.parser.TestCaseIterator;
import jp.vmi.selenium.selenese.parser.TestElementIteratorFactory;
import jp.vmi.selenium.selenese.parser.TestProjectReader;

/**
 * Parser of test-suite script.
 */
public final class TestSuiteParser {

    /**
     * Parse test-suite script.
     *
     * @param sourceType test-suite source type.
     * @param caseIter test-case iterator.
     * @param projectReader test-project reader.
     * @param commandFactory command factory.
     * @return Selenese instance.
     */
    public static Selenese parse(SourceType sourceType, TestCaseIterator caseIter, TestProjectReader projectReader, ICommandFactory commandFactory) {
        TestSuite testSuite = Binder.newTestSuite(caseIter.getFilename(), caseIter.getName());
        testSuite.setId(caseIter.getId());
        try {
            TestElementIteratorFactory<CommandIterator, TestCaseEntry> comIterFactory = caseIter.getCommandIteratorFactory();
            for (TestCaseEntry entry : caseIter) {
                CommandIterator comIter = comIterFactory.getTestElementIterator(entry);
                Selenese selenese = TestCaseParser.parse(sourceType, comIter, projectReader, commandFactory);
                testSuite.addSelenese(selenese);
            }
        } catch (InvalidSeleneseException e) {
            testSuite.addSelenese(Binder.newErrorTestCase(e.getFilename(), e));
        }
        return caseIter.isDummy() ? testSuite.getSeleneseList().get(0) : testSuite;
    }
}
