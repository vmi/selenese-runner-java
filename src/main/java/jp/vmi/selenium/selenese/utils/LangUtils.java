package jp.vmi.selenium.selenese.utils;

import java.util.Iterator;
import java.util.Locale;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Strings;

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
     * Checks if s is null, empty or whitespace only.
     *
     * @param s a string or null.
     * @return true if s is null, empty string, or whitespace only.
     */
    public static boolean isBlank(String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    /**
     * Checks if s contains ss without case sensitivity.
     *
     * @param s a string.
     * @param ss a substring.
     * @return true if s contains ss without case sensitivity.
     */
    public static boolean containsIgnoreCase(String s, String ss) {
        if (s == null || ss == null)
            return false;
        return s.toLowerCase(Locale.ROOT).contains(ss.toLowerCase(Locale.ROOT));
    }

    /**
     * Capitalize string.
     *
     * @param s a string.
     * @return capitalized string.
     */
    public static String capitalize(String s) {
        if (Strings.isNullOrEmpty(s))
            return s;
        int ch = s.codePointAt(0);
        if ((Character.getType(ch) & Character.LOWERCASE_LETTER) == 0)
            return s;
        ch = Character.toUpperCase(ch);
        if (Character.isBmpCodePoint(ch))
            return ((char) ch) + s.substring(1);
        else
            return Character.highSurrogate(ch) + Character.lowSurrogate(ch) + s.substring(2);
    }

    /**
     * Uncapitalize string.
     *
     * @param s a string.
     * @return uncapitalized string.
     */
    public static String uncapitalize(String s) {
        if (Strings.isNullOrEmpty(s))
            return s;
        int ch = s.codePointAt(0);
        if ((Character.getType(ch) & Character.UPPERCASE_LETTER) == 0)
            return s;
        ch = Character.toLowerCase(ch);
        if (Character.isBmpCodePoint(ch))
            return ((char) ch) + s.substring(1);
        else
            return Character.highSurrogate(ch) + Character.lowSurrogate(ch) + s.substring(2);
    }

    /**
     * Join stream to string.
     *
     * @param delimiter delimiter
     * @param stream stream
     * @return joined string
     */
    public static String join(CharSequence delimiter, Stream<?> stream) {
        return stream.map(item -> item.toString()).collect(Collectors.joining(delimiter));
    }

    /**
     * Join iterator to string.
     *
     * @param delimiter delimiter
     * @param iterator iterator
     * @return joined string
     */
    public static String join(CharSequence delimiter, Iterator<?> iterator) {
        Spliterator<?> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        Stream<?> stream = StreamSupport.stream(spliterator, false);
        return join(delimiter, stream);
    }

    /**
     * Join iterable to string.
     *
     * @param delimiter delimiter
     * @param iterable iterable
     * @return joined string
     */
    public static String join(CharSequence delimiter, Iterable<?> iterable) {
        return join(delimiter, iterable.iterator());
    }
}
