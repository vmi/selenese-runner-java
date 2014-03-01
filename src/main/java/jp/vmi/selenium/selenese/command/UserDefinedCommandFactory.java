package jp.vmi.selenium.selenese.command;

/**
 * Factory for user defined command.
 * 
 * @deprecated use {@link ICommandFactory}.
 */
@Deprecated
public interface UserDefinedCommandFactory {

    /**
     * Constructs selenese command.
     *
     * @param index index in selenese script file.
     * @param name command name.
     * @param args command arguments.
     * @return Command instance.
     */
    Command newCommand(int index, String name, String... args);
}
