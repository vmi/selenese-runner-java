package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.locator.Locator;
import jp.vmi.selenium.selenese.result.Result;

/**
 * Command interface.
 */
public interface ICommand {

    /**
     * Get source elements.
     *
     * @return array of source elements. (always 3 elements)
     */
    String[] getSource();

    /**
     * Get index in selenese script file (1 origin).
     *
     * @return index.
     */
    int getIndex();

    /**
     * Get command arguments.
     *
     * @return command arguments.
     */
    String[] getArguments();

    /**
     * Get command name.
     *
     * @return command name.
     */
    String getName();

    /**
     * Extract locators from arguments.
     *
     * @param args the command arguments.
     * @return extracted parsed locators.
     */
    Locator[] extractLocators(String[] args);

    /**
     * May the command update screen?
     *
     * @return true if the command may update screen.
     */
    boolean mayUpdateScreen();

    /**
     * Execute the command.
     * <p>
     * Note: set the command result to testCase in this method.
     * </p>
     *
     * @param context Selenese Runner context.
     * @param curArgs current arugments. (the variables in it are extracted)
     * @return the command result.
     */
    Result execute(Context context, String... curArgs);
    /**
     * Set beginning-of-loop command.
     *
     * @param startLoop beginning-of-loop command.
     */
    void setStartLoop(StartLoop startLoop);

    /**
     * Get beginning-of-loop command.
     *
     * @return beginning-of-loop command.
     */
    StartLoop getStartLoop();

}
