package jp.vmi.selenium.selenese.utils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  String escape utilities.
 */
public final class EscapeUtils {

    private EscapeUtils() {
        // no operation
    }

    /** Regex pattern for HTML escape. */
    public static final Pattern HTML_RE = Pattern.compile("([<>&\"'\\\\])|\r?\n|\r");

    /** Mapping table for HTML escape. */
    public static final Map<String, String> HTML_ESC_MAP = new HashMap<>();

    static {
        HTML_ESC_MAP.put("<", "&lt;");
        HTML_ESC_MAP.put(">", "&gt;");
        HTML_ESC_MAP.put("&", "&amp;");
        HTML_ESC_MAP.put("\"", "&quot;");
        HTML_ESC_MAP.put("'", "&#39;");
        HTML_ESC_MAP.put("\\", "&#92;");
        HTML_ESC_MAP.put("\r\n", "<br>\n");
        HTML_ESC_MAP.put("\n", "<br>\n");
        HTML_ESC_MAP.put("\r", "<br>\n");
    }

    // Regex pattern for URI encode.
    //
    // This pattern equivalent to JavaScript encodeURI().
    // see: https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/encodeURI
    private static Pattern URI_RE = Pattern.compile("[^;,/?:@&=+$A-Za-z0-9\\-_.!~*'()#]+");

    private static Pattern JSSTR_RE = Pattern.compile("[\\\"]");

    /**
     * HTML escape.
     *
     * @param s raw string.
     * @param newline escape newline to &lt;br&gt;.
     * @return HTML escaped string.
     */
    public static String escapeHtml(String s, boolean newline) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = HTML_RE.matcher(s);
        int index = 0;
        while (matcher.find(index)) {
            int start = matcher.start();
            if (index < start)
                result.append(s, index, start);
            if (newline) {
                result.append(HTML_ESC_MAP.get(matcher.group()));
            } else {
                String specials = matcher.group(1);
                if (specials != null)
                    result.append(HTML_ESC_MAP.get(specials));
                else
                    result.append('\n');
            }
            index = matcher.end();
        }
        if (index < s.length())
            result.append(s, index, s.length());
        return result.toString();
    }

    /**
     * URI encode.
     *
     * @param s raw string.
     * @return URI encoded string.
     */
    public static String encodeUri(String s) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = URI_RE.matcher(s);
        int index = 0;
        while (matcher.find(index)) {
            int start = matcher.start();
            if (index < start)
                result.append(s, index, start);
            for (byte b : matcher.group().getBytes(StandardCharsets.UTF_8))
                result.append(String.format("%%%02x", b));
            index = matcher.end();
        }
        if (index < s.length())
            result.append(s, index, s.length());
        return result.toString();
    }

    /**
     * JavaScript String.
     *
     * @param s raw string.
     * @return JS escaped string.
     */
    public static String escapeJSString(String s) {
        Matcher matcher = JSSTR_RE.matcher(s);
        if (!matcher.find())
            return s;
        StringBuilder result = new StringBuilder();
        int beginIndex = 0;
        do {
            int endIndex = matcher.start();
            if (beginIndex < endIndex)
                result.append(s.substring(beginIndex, endIndex));
            result.append('\\').append(matcher.group());
            beginIndex = matcher.end();
        } while (matcher.find(beginIndex));
        if (beginIndex < s.length())
            result.append(s.substring(beginIndex));
        return result.toString();
    }
}
