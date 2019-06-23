package jp.vmi.selenium.selenese.parser;

import java.util.Arrays;

import jp.vmi.selenium.runner.model.side.SideCommand;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.command.ICommandFactory;

/**
 * Command entry in test case.
 */
public abstract class CommandEntry extends TestElementEntry {

    CommandEntry(String id, String name) {
        super(id, name);
    }

    /**
     * Add command to test case.
     *
     * @param testCase test case.
     * @param commandFactory command factory.
     */
    public abstract void addToTestCase(TestCase testCase, ICommandFactory commandFactory);

    /**
     * Create new CommandEntry instance.
     *
     * @param id comman id.
     * @param name command name.
     * @param args command arguments.
     * @return CommandEntry instance.
     */
    public static CommandEntry newInstance(String id, String name, String... args) {
        return new SeleneseCommandEntry(id, name, args);
    }

    /**
     * Create new CommandEntry instance.
     *
     * @param sideCommand SIDE command.
     * @return CommandEntry instance.
     */
    public static CommandEntry newInstance(SideCommand sideCommand) {
        return new SideCommandEntry(sideCommand);
    }

    static class SeleneseCommandEntry extends CommandEntry {

        public final String[] args;

        SeleneseCommandEntry(String id, String name, String[] args) {
            super(id, name);
            this.args = Arrays.stream(args).map(item -> item != null ? item : "").toArray(String[]::new);
        }

        @Override
        public void addToTestCase(TestCase testCase, ICommandFactory commandFactory) {
            testCase.addCommand(index -> commandFactory.newCommand(index, name, args));
        }
    }

    static class SideCommandEntry extends CommandEntry {

        public final SideCommand sideCommand;

        SideCommandEntry(SideCommand sideCommand) {
            super(sideCommand.getId(), sideCommand.getCommand());
            this.sideCommand = sideCommand;
        }

        private static String toNonNullString(String s) {
            return s != null ? s : "";
        }

        @Override
        public void addToTestCase(TestCase testCase, ICommandFactory commandFactory) {
            String comment = sideCommand.getComment();
            if (comment != null && !comment.isEmpty())
                testCase.addCommand(index -> commandFactory.newCommand(index, "comment", comment));
            testCase.addCommand(index -> {
                ICommand command = commandFactory.newCommand(
                    index, name,
                    toNonNullString(sideCommand.getTarget()),
                    toNonNullString(sideCommand.getValue()));
                command.setSideCommand(sideCommand);
                return command;
            });
        }

    }
}
