package jp.vmi.script;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for JavaScript object.
 */
public class JSWrapper {

    private static final Logger log = LoggerFactory.getLogger(JSWrapper.class);

    /** wrapped object name. */
    public static final String OBJECT = "object";

    /** arguments name. */
    public static final String ARGS = "args";

    private final ScriptEngine engine;
    private final Object object;
    private final Bindings bindings;

    /**
     * Constructor.
     *
     * @param engine script engine.
     * @param object wrapped JS object.
     */
    public JSWrapper(ScriptEngine engine, Object object) {
        this.engine = engine;
        this.object = object;
        this.bindings = engine.createBindings();
        this.bindings.put(OBJECT, object);
    }

    /**
     * Evaluate script.
     *
     * @param <T> result type.
     * @param script script.
     * @param args arguments.
     * @return result.
     */
    @SuppressWarnings("unchecked")
    public <T> T eval(String script, Object... args) {
        try {
            bindings.put(ARGS, args);
            return (T) engine.eval(script, bindings);
        } catch (ScriptException e) {
            log.info("Failed: [{}] - {}", script, e);
            return null;
        }
    }

    /**
     * Unwrap object.
     *
     * @return unwrapped object.
     */
    public Object unwrap() {
        return object;
    }
}
