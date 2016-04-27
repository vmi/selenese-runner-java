package jp.vmi.selenium.selenese.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for Selenium.
 */
public class SeleniumUtils {

    private SeleniumUtils() {
    }

    /**
     * string-matching pattern of SeleniumIDE.
     */
    public static class SeleniumPattern {

        @SuppressWarnings("javadoc")
        public static enum Type {
            REGEXP, REGEXPI, GLOB, EXACT
        }

        /**
         * Type of string-matching pattern.
         */
        public final Type type;

        /**
         * Regular Expression of pattern.
         */
        public final Pattern regexpPattern;

        /**
         * String of pattern.
         */
        public final String stringPattern;

        /**
         * Constructor.
         *
         * @param pattern string-matching pattern.
         */
        public SeleniumPattern(String pattern) {
            String[] p = pattern.split(":", 2);
            if (p.length == 2) {
                String type = p[0].toLowerCase();
                if ("regexp".equals(type)) {
                    this.type = Type.REGEXP;
                    this.regexpPattern = Pattern.compile(p[1]);
                    this.stringPattern = p[1];
                    return;
                } else if ("regexpi".equals(type)) {
                    this.type = Type.REGEXPI;
                    this.regexpPattern = Pattern.compile(p[1], Pattern.CASE_INSENSITIVE);
                    this.stringPattern = p[1];
                    return;
                } else if ("glob".equals(type)) {
                    pattern = p[1];
                    // don't return here.
                } else if ("exact".equals(type)) {
                    this.type = Type.EXACT;
                    this.regexpPattern = null;
                    this.stringPattern = p[1];
                    return;
                }
            }
            if (pattern.indexOf('*') >= 0 || pattern.indexOf('?') >= 0) {
                this.type = Type.GLOB;
                // see http://stackoverflow.com/a/3619098
                StringBuilder re = new StringBuilder("\\A\\Q");
                re.append(pattern.replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q"));
                if (re.length() >= 6 && re.charAt(4) == '\\' && re.charAt(5) == 'E')
                    re.delete(2, 6);
                int len = re.length();
                if (re.charAt(len - 2) == '\\' && re.charAt(len - 1) == 'Q')
                    re.setCharAt(re.length() - 1, 'z');
                else
                    re.append("\\E\\z");
                this.regexpPattern = Pattern.compile(re.toString(), Pattern.DOTALL);
            } else {
                this.type = Type.EXACT;
                this.regexpPattern = null;
            }
            this.stringPattern = pattern;
        }

        /**
         * Match pattern.
         *
         * @param input input string.
         * @return true if matched.
         */
        public boolean matches(String input) {
            input = normalizeSpaces(input);
            switch (type) {
            case REGEXP:
            case REGEXPI:
            case GLOB:
                return regexpPattern.matcher(input).find();
            case EXACT:
                return normalizeSpaces(stringPattern).equals(input);
            default:
                throw new UnsupportedOperationException(type.toString());
            }
        }

        @Override
        public String toString() {
            return "SeleniumPattern[" + type + ":" + StringEscapeUtils.escapeJava(stringPattern) + "]";
        }
    }

    /**
     * String-match pattern.
     *
     * @param pattern pattern. prefix is "glob:", "regexp:", "regexpi:", or "exact:".
     * @param input input string.
     * @return true if matched pattern.
     */
    public static boolean patternMatches(String pattern, CharSequence input) {
        return new SeleniumPattern(pattern).matches(input.toString());
    }

    /**
     * Unifty U+0020 and U+00A0, Trim, and compress spaces in string.
     *
     * @param str string.
     * @return normalized string.
     */
    public static String normalizeSpaces(String str) {
        int si = str.indexOf(' ');
        int ni = str.indexOf('\u00A0');
        if (si < 0 && ni < 0)
            return str;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        boolean skipSpc = true;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\u00A0') {
                buf.append(c);
                skipSpc = false;
            } else if (!skipSpc) {
                buf.append(' ');
                skipSpc = true;
            }
            // skip if skipSpc && (c == ' ' || c == '\u00A0)
        }
        int blen = buf.length();
        if (blen > 0 && buf.charAt(blen - 1) == ' ')
            buf.deleteCharAt(blen - 1);
        return buf.toString();
    }

    /**
     * Convert to String from the result of execute().
     *
     * @param <T> the type of result object.
     * @param result the result of execute().
     * @return converted string.
     */
    public static <T> String convertToString(T result) {
        if (result == null)
            return "";
        else if (result instanceof Object[])
            return StringUtils.join((Object[]) result, ',');
        else if (result instanceof Iterable)
            return StringUtils.join((Iterable<?>) result, ',');
        else if (result instanceof Iterator)
            return StringUtils.join((Iterator<?>) result, ',');
        else
            return result.toString();
    }

    /**
     * Test current JVM version 7 or later.
     *
     * @return true if JVM version is 7 or later.
     *
     * @deprecated remove calling this method in caller.
     */
    @Deprecated
    public static boolean isJava7orLater() {
        return true;
    }

    private static final Pattern BEGIN_RE = Pattern.compile("function\\s+(?<name>\\w+)\\(.*?\\)\\s*\\{\\s*//\\s*BEGIN\\s*");
    private static final Pattern END_RE = Pattern.compile("\\}\\s*//\\s*END\\s*");

    /**
     * Load JavaScript and put each function to map.
     * @param is InputStream.
     * @return map of function name and body.
     */
    public static Map<String, String> loadJS(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Map<String, String> result = new HashMap<>();
        String line;
        String name = null;
        StringBuilder body = null;
        try {
            while ((line = br.readLine()) != null) {
                if (name == null) {
                    Matcher matcher = BEGIN_RE.matcher(line);
                    if (matcher.matches()) {
                        name = matcher.group("name");
                        body = new StringBuilder();
                    }
                } else {
                    if (END_RE.matcher(line).matches()) {
                        result.put(name, body.toString());
                        name = null;
                        body = null;
                    } else {
                        body.append(line.trim()).append('\n');
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
