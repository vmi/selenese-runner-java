package jp.vmi.selenium.selenese.utils;

/**
 * Utilities for logging.
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    /**
     * quote backslash and doublequote.
     *
     * @param str raw string.
     * @return quoted string.
     */
    public static String quote(String str) {
        return "\"" + str.replaceAll("([\\\\\"])", "\\\\$1") + "\"";
    }

    /**
     * quote backslash and doublequote for each strings.
     *
     * @param strs raw strings.
     * @return quoted strings.
     */
    public static String[] quote(String[] strs) {
        int len = strs.length;
        String[] result = new String[len];
        for (int i = 0; i < len; i++)
            result[i] = quote(strs[i]);
        return result;
    }
}
