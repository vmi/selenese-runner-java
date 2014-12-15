package jp.vmi.selenium.selenese.command;

/**
 * Interface of command factory.
 */
public interface ICommandFactory {

    /**
     * Constructs selenese command.
     *
     * @param index index in selenese script file.
     * @param name command name.
     * @param args command arguments.
     * @return ICommand instance or null.
     */
    ICommand newCommand(int index, String name, String... args);
}
