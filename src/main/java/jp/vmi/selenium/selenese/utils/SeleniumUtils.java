package jp.vmi.selenium.selenese.utils;

import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Utililities for Selenium.
 */
public class SeleniumUtils {

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
            switch (type) {
            case REGEXP:
            case REGEXPI:
            case GLOB:
                return regexpPattern.matcher(input).find();
            case EXACT:
                return stringPattern.equals(input);
            default:
                throw new UnsupportedOperationException(type.toString());
            }
        }

        @Override
        public String toString() {
            return "SeleniumPattern[" + type + ":" + stringPattern + "]";
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
     */
    public static boolean isJava7orLater() {
        String[] version = SystemUtils.JAVA_VERSION.split("[._]");
        int major = NumberUtils.toInt(version[0]);
        if (version.length < 2 || major == 0)
            throw new UnsupportedOperationException("Can't parse Java version string: " + SystemUtils.JAVA_VERSION);
        return major >= 2 || NumberUtils.toInt(version[1]) >= 7;
    }
}
