package jp.vmi.script;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;

import static jp.vmi.script.JSWrapper.*;

/**
 * Wrapper for JavaScript map.
 *
 * @param <K> the type of key.
 * @param <V> the type of value.
 */
public class JSMap<K, V> extends AbstractMap<K, V> {

    private final JSWrapper wrapper;

    /**
     * Constructor.
     *
     * @param engine script engine.
     * @param object JavaScript object.
     */
    public JSMap(ScriptEngine engine, Object object) {
        wrapper = new JSWrapper(engine, object);
    }

    @Override
    public V get(Object key) {
        return wrapper.eval(OBJECT + "[" + ARGS + "[0]]", key);
    }

    // for internal use only, but ScriptEngine requires public scope.
    @SuppressWarnings("javadoc")
    public static interface EntrySetCallback<K, V> {
        public void execute(K key, V value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K, V>> result = new HashSet<>();
        wrapper.eval("for (var k in " + OBJECT + ") {"
            + "  if (" + OBJECT + ".hasOwnProperty(k)) {"
            + "    " + ARGS + "[0].execute(k, " + OBJECT + "[k]);"
            + "  }"
            + "}",
            new EntrySetCallback<K, V>() {
                @Override
                public void execute(K key, V value) {
                    result.add(new SimpleEntry<>(key, value));
                }
            });
        return result;
    }

    /**
     * Unwrap object.
     *
     * @return unwrapped object.
     */
    public Object unwrap() {
        return wrapper.unwrap();
    }

    /**
     * JavaScript object to Map.
     *
     * @param <K> the type of key.
     * @param <V> the type of value.
     * @param engine Script engine.
     * @param object JavaScript object.
     * @return Map object.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(ScriptEngine engine, Object object) {
        if (object == null)
            return null;
        else if (object instanceof Map)
            return (Map<K, V>) object; // for Rhino in Java7 and Nashorn.
        else
            return new JSMap<>(engine, object);
    }
}
