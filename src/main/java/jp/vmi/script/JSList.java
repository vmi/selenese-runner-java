package jp.vmi.script;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import static jp.vmi.script.JSWrapper.*;

/**
 * Wrapper for JavaScript array object.
 *
 * @param <E> the type of array element.
 */
public abstract class JSList<E> extends AbstractList<E> {

    /**
     * Unwrap object.
     *
     * @return unwrapped object.
     */
    public abstract Object unwrap();

    // for Nashorn.
    static class JSMapList<E> extends JSList<E> {

        private final Map<?, E> map;

        @SuppressWarnings("unchecked")
        JSMapList(Object object) {
            this.map = (Map<?, E>) object;
        }

        @Override
        public E get(int index) {
            return map.get(Integer.toString(index));
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public Object unwrap() {
            return map;
        }
    }

    // for Rhino in Java6.
    static class JSNativeList<E> extends JSList<E> {

        private final JSWrapper wrapper;

        JSNativeList(ScriptEngine engine, Object object) {
            wrapper = new JSWrapper(engine, object);
        }

        @Override
        public E get(int index) {
            return wrapper.eval(OBJECT + "[" + ARGS + "[0]]", index);
        }

        @Override
        public int size() {
            Number result = wrapper.eval(OBJECT + ".length");
            return result.intValue();
        }

        @Override
        public Object unwrap() {
            return wrapper.unwrap();
        }
    }

    /**
     * JavaScript object to List.
     *
     * @param <E> the type of list element.
     * @param engine script engine.
     * @param object JavaScript object.
     * @return List object.
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> toList(ScriptEngine engine, Object object) {
        if (object == null)
            return null;
        else if (object instanceof List)
            return (List<E>) object; // for Rhino in Java7.
        else if (object instanceof Map)
            return new JSMapList<>(object);
        else
            return new JSNativeList<>(engine, object);
    }
}
