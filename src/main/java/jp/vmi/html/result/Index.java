package jp.vmi.html.result;

/**
 * Object with index.
 * 
 * @param <T> type in WithIndex.
 */
public class Index<T> {
    private final int index;
    private final T object;

    /**
     * Constructor.
     *
     * @param index index.
     * @param object object.
     */
    public Index(int index, T object) {
        this.index = index;
        this.object = object;
    }

    /**
     * Get index.
     *
     * @return index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get object.
     *
     * @return object.
     */
    public T getObject() {
        return object;
    }
}
