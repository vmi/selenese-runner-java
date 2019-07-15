package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.parser.CommandEntry;
import jp.vmi.selenium.selenese.parser.CommandIterator;
import jp.vmi.selenium.selenese.parser.TestProjectReader;

/**
 * Parser of test-case script.
 */
public final class TestCaseParser {

    /**
     * Parse test-case script.
     *
     * @param sourceType test-case source type.
     * @param iter command iterator.
     * @param projectReader test-project reader.
     * @param commandFactory command factory.
     * @return Selenese instance.
     */
    public static Selenese parse(SourceType sourceType, CommandIterator iter, TestProjectReader projectReader, ICommandFactory commandFactory) {
        String id = iter.getId();
        TestCase testCase = projectReader.getTestCaseById(id);
        if (testCase != null)
            return testCase;
        testCase = Binder.newTestCase(sourceType, iter.getFilename(), iter.getName(), iter.getBaseURL());
        testCase.setId(id);
        try {
            for (CommandEntry entry : iter) {
                entry.addToTestCase(testCase, commandFactory);
            }
        } catch (RuntimeException e) {
            return Binder.newErrorTestCase(iter.getFilename(), new InvalidSeleneseException(e, iter.getFilename(), iter.getName()));
        }
        projectReader.addTestCase(testCase);
        return testCase;
    }
}
