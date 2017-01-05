package jp.vmi.selenium.selenese.utils;

/**
 * Language utilities.
 */
public class LangUtils {

    private LangUtils() {
    }

    /**
     * Empty String array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Null to empty string.
     *
     * @param s a string or null.
     * @return a string or empty string.
     */
    public static String nullToEmpty(String s) {
        return s != null ? s : "";
    }
}
