package jp.vmi.selenium.selenese;

import jp.vmi.selenium.selenese.result.Result;

/**
 * Selenese script interface.
 */
public interface Selenese {

    /**
     * Selenese script types.
     */
    @SuppressWarnings("javadoc")
    public enum Type {
        TEST_SUITE, TEST_CASE
    }

    /**
     * Get script type.
     *
     * @return script type.
     */
    Type getType();

    /**
     * Get file base name.
     *
     * For example, return "def" when file name is "/abc/def.html".
     *
     * @return file base name.
     */
    String getBaseName();

    /**
     * Get script name.
     *
     * @return script name.
     */
    String getName();

    /**
     * Is error instance?
     *
     * @return true if this is error instance.
     */
    boolean isError();

    /**
     * Execute script.
     * @param parent parent selenese instance or null.
     * @param context Selenese Runner context.
     * @exception InvalidSeleneseException failed in parsing or executing selenese.
     * @return result.
     */
    Result execute(Selenese parent, Context context) throws InvalidSeleneseException;

    /**
     * Get selenese result.
     *
     * @return selenese result.
     */
    Result getResult();
}
