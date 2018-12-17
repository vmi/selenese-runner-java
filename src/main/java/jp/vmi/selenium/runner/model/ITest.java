package jp.vmi.selenium.runner.model;

import java.util.List;

/**
 * Test case.
 *
 * @param <C> type of command.
 */
public interface ITest<C extends ICommand> extends Id {

    /**
     * Get list of commands.
     *
     * @return list of commands.
     */
    List<C> getCommands();
}
