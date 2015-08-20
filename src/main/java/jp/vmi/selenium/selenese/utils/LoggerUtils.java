package jp.vmi.selenium.selenese.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Utilities for logging.
 */
public final class LoggerUtils {

    private LoggerUtils() {
    }

    /**
     * switch java.util.logging handler to slf4j.
     */
    public static void initLogger() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static final Pattern META_CHAR_RE = Pattern.compile("[\u0000-\u001F\u0080\\\\\"]");

    /**
     * quote control code, backslash and doublequote.
     *
     * @param buffer result buffer.
     * @param str raw string.
     * @return same as buffer.
     */
    public static StringBuilder quote(StringBuilder buffer, String str) {
        buffer.append('"');
        Matcher matcher = META_CHAR_RE.matcher(str);
        int start;
        for (start = 0; matcher.find(start); start = matcher.end()) {
            int end = matcher.start();
            if (start < end)
                buffer.append(str.substring(start, end));
            buffer.append('\\');
            char ch = matcher.group().charAt(0);
            switch (ch) {
            case '\\':
            case '"':
                buffer.append(ch);
                break;
            case '\t':
                buffer.append('t');
                break;
            case '\r':
                buffer.append('r');
                break;
            case '\n':
                buffer.append('n');
                break;
            default:
                buffer.append(String.format("x%02X", ch));
                break;
            }
        }
        buffer.append(str.substring(start));
        return buffer.append('"');
    }

    /**
     * quote control code, backslash and doublequote.
     *
     * @param str raw string.
     * @return quoted string.
     */
    public static String quote(String str) {
        return quote(new StringBuilder(str.length() + 8), str).toString();
    }

    /**
     * quote control code, backslash and doublequote for each strings.
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
