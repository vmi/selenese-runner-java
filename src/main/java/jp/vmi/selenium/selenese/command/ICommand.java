package jp.vmi.selenium.selenese.command;

import jp.vmi.selenium.selenese.Context;
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
     * Convert locators from arguments.
     * 
     * @param args the command arguments.
     * @return converted locators.
     */
    String[] convertLocators(String[] args);

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
     * Get the command result.
     * 
     * @return the command result.
     */
    Result getResult();
}
