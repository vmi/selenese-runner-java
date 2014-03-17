package jp.vmi.selenium.rollup;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.script.JSArray;

/**
 * Rollup manager called by user-extention-rollup.js.
 */
public class RollupManager {

    private static final Logger log = LoggerFactory.getLogger(RollupManager.class);

    private static RollupRules currentRollupRules = null;

    /**
     * Execute closure in the rollupRules context.
     *
     * @param rollupRules rollupRules context.
     * @param closure closure.
     */
    public static synchronized void rollupRulesContext(RollupRules rollupRules, Closure closure) {
        currentRollupRules = rollupRules;
        try {
            closure.execute(null);
        } finally {
            currentRollupRules = null;
        }
    }

    /**
     * Add rollup rule.
     *
     * @param rule rollup rule.
     */
    public void addRollupRule(Map<?, ?> rule) {
        currentRollupRules.addRule(rule);
        log.info("Added rollup rule: {}", rule.get("name"));
        log.info("- Description: {}", rule.get("description"));
        List<Map<String, String>> args = JSArray.wrap(rule.get("args"));
        if (args != null && args.size() > 0) {
            log.info("- Arguments:");
            for (Map<String, String> arg : args) {
                log.info("  + {}: {}", arg.get("name"), arg.get("description"));
            }
        }
        // TODO commandMatchers
        if (rule.containsKey("expandedCommands")) {
            log.info("- Expanded commands: array");
        } else if (rule.containsKey("getExpandedCommands")) {
            log.info("- Expanded commands: function");
        } else {
            throw new SeleniumException("Missing expandedCommands nor getExpandedCommands in rollup rule definition.");
        }
    }
}
