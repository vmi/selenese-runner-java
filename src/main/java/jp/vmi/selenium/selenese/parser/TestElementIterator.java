package jp.vmi.selenium.selenese.parser;

import java.util.Iterator;

/**
 * Test element iterator.
 *
 * @param <E> Type of test element.
 */
public interface TestElementIterator<E extends TestElementEntry> extends Iterator<E>, Iterable<E> {

    /**
     * Get filename of this test element.
     *
     * @return filename.
     */
    String getFilename();

    /**
     * Get name of this test element.
     *
     * @return name.
     */
    String getName();

    /**
     * Get id of this test element.
     *
     * @return id.
     */
    String getId();

    /**
     * Is this dummy iterator?
     *
     * @return true if this is dummy iterator.
     */
    default boolean isDummy() {
        return false;
    }

    @Override
    default Iterator<E> iterator() {
        return this;
    }
}
