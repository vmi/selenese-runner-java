package jp.vmi.selenium.selenese.command;

/**
 * Factory for user defined command.
 */
public interface UserDefinedCommandFactory {

    /**
     * Constructs selenese command.
     *
     * @param index index in selenese script file.
     * @param name command name.
     * @param args command arguments.
     * @return Command instance or null. (null if unsupported command)
     */
    Command newCommand(int index, String name, String... args);
}