package jp.vmi.selenium.rollup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.collections.Closure;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Set or rollup rules.
 */
public class RollupRules {

    private final ScriptEngine engine;
    private final Map<String, RollupRule> rollupRules = new HashMap<String, RollupRule>();

    /**
     * Constructor.
     */
    public RollupRules() {
        engine = new ScriptEngineManager().getEngineByExtension("js");
    }

    /**
     * Load rollup rule definitions.
     *
     * @param is InputStream object.
     */
    public void load(final InputStream is) {
        RollupManager.rollupRulesContext(this, new Closure() {
            @Override
            public void execute(Object input) {
                Reader r = null;
                try {
                    String packageName = RollupManager.class.getPackage().getName();
                    engine.eval("importPackage(Packages." + packageName + ");");
                    r = new InputStreamReader(is, Charsets.UTF_8);
                    engine.eval(r);
                } catch (ScriptException e) {
                    throw new SeleniumException(e);
                } finally {
                    IOUtils.closeQuietly(r);
                }
            }
        });
    }

    /**
     * Load rollup rule definitions.
     *
     * @param filename JavaScript file for rollup rule definitions. 
     */
    public void load(String filename) {
        try {
            load(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new SeleniumException(e);
        }
    }

    /**
     * Add rollup rule.
     *
     * @param rule rollup rule.
     */
    public void addRule(Map<?, ?> rule) {
        rollupRules.put((String) rule.get("name"), new RollupRule(engine, rule));
    }

    /**
     * Get rollup rule.
     *
     * @param name rollup rule name.
     * @return IRollupRule object.
     */
    public IRollupRule get(String name) {
        return rollupRules.get(name);
    }
}
