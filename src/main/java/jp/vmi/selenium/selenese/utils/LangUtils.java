package jp.vmi.selenium.selenese.utils;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
     * @return true if s is null or empty string.
     */
    public static String nullToEmpty(String s) {
        return s != null ? s : "";
    }

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
