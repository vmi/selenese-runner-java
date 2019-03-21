package jp.vmi.selenium.selenese.subcommand;

import jp.vmi.selenium.selenese.ArgumentInfo;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.ArgumentType;

/**
 * Sub-command interface.
 *
 * @param <T> the result type of this sub-command.
 */
public interface ISubCommand<T> extends ArgumentInfo {

    /**
     * Get sub-command name.
     *
     * @return sub-command name.
     */
    String getName();

    /**
     * Get arguments of this sub-command.
     *
     * @return arguments of thie sub-command.
     */
    ArgumentType[] getArgumentTypes();

    /**
     * Execute sub command.
     *
     * @param context Selenese Runner context.
     * @param args arguments.
     * @return sub command result.
     */
    T execute(Context context, String... args);
}
