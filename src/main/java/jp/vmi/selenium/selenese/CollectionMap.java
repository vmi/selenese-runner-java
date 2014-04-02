package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

/**
 * Collection Map.
 */
public class CollectionMap extends HashMap<String, Deque<String>> {

    private static final long serialVersionUID = 1L;

    /**
     * Create new collection (FIFO).
     *
     * @param collectionName collection name.
     */
    public void addCollection(String collectionName) {
        put(collectionName, new ArrayDeque<String>());
    }

    /**
     * Add value to collection.
     *
     * @param collectionName collection name.
     * @param value value.
     */
    public void addToCollection(String collectionName, String value) {
        Deque<String> collection = get(collectionName);
        collection.addLast(value);
    }

    /**
     * Poll value from collection.
     *
     * @param collectionName collection name.
     * @return value.
     */
    public String pollFromCollection(String collectionName) {
        Deque<String> collection = get(collectionName);
        return collection.pollFirst();
    }
}
