package jp.vmi.html.result;

import java.util.Iterator;

/**
 * Add index to iterable object.
 *
 * @param <T> type in iterable.
 */
public class WithIndex<T> implements Iterable<Index<T>> {

    private final Iterable<T> iterable;

    /**
     * Constructor.
     * 
     * @param iterable iterable object.
     */
    public WithIndex(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    @Override
    public Iterator<Index<T>> iterator() {
        return new Iterator<Index<T>>() {
            private int index = 0;
            private final Iterator<T> iterator = iterable.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Index<T> next() {
                return new Index<T>(index++, iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
}
