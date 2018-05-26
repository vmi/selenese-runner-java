package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.selenese.parser.CommandEntry;
import jp.vmi.selenium.selenese.parser.CommandIterator;

/**
 * Parser of test-case script.
 */
public final class TestCaseParser {

    /**
     * Parse test-case script.
     *
     * @param iter command iterator.
     * @param commandFactory command factory.
     * @return Selenese instance.
     */
    public static Selenese parse(CommandIterator iter, ICommandFactory commandFactory) {
        TestCase testCase = Binder.newTestCase(iter.getFilename(), iter.getName(), iter.getBaseURL());
        try {
            for (CommandEntry entry : iter) {
                if (entry.comment != null && !entry.comment.isEmpty())
                    testCase.addCommand(commandFactory, "comment", entry.comment);
                testCase.addCommand(commandFactory, entry.name, entry.args);
            }
        } catch (RuntimeException e) {
            return Binder.newErrorTestCase(iter.getFilename(), new InvalidSeleneseException(e, iter.getFilename(), iter.getName()));
        }
        return testCase;
    }
}
