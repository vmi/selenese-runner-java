package jp.vmi.script;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for JavaScript array object.
 * 
 * @param <E> the type of array element.
 */
public abstract class JSArray<E> extends AbstractList<E> {

    private static class JSListArray<E> extends JSArray<E> {

        private final List<E> list;

        @SuppressWarnings("unchecked")
        private JSListArray(Object object) {
            this.list = (List<E>) object;
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }

    }

    private static class JSMapArray<E> extends JSArray<E> {

        private final Map<?, E> map;

        @SuppressWarnings("unchecked")
        private JSMapArray(Object object) {
            this.map = (Map<?, E>) object;
        }

        @Override
        public E get(int index) {
            return map.get(index);
        }

        @Override
        public int size() {
            return map.size();
        }
    }

    /**
     * Wrap JavaScript array object.
     *
     * @param <E> the type of array element.
     * @param object JavaScript array object.
     * @return wrapped class instance.
     */
    public static <E> JSArray<E> wrap(Object object) {
        if (object == null)
            return null;
        else if (object instanceof List)
            return new JSListArray<E>(object);
        else if (object instanceof Map)
            return new JSMapArray<E>(object);
        else
            throw new IllegalArgumentException("Unsupported object: " + object);
    }
}
