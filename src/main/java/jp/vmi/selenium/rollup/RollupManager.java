package jp.vmi.selenium.rollup;

import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.script.JSList;
import jp.vmi.script.JSMap;

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
     * @param runnable runnable.
     */
    public static synchronized void rollupRulesContext(RollupRules rollupRules, Runnable runnable) {
        currentRollupRules = rollupRules;
        try {
            runnable.run();
        } finally {
            currentRollupRules = null;
        }
    }

    /**
     * Add rollup rule.
     *
     * @param rule rollup rule.
     */
    public void addRollupRule(Object rule) {
        ScriptEngine engine = currentRollupRules.engine;
        Map<?, ?> ruleMap = JSMap.toMap(engine, rule);
        currentRollupRules.addRule(ruleMap);
        log.info("Added rollup rule: {}", ruleMap.get("name"));
        log.info("- Description: {}", ruleMap.get("description"));
        List<Object> args = JSList.toList(engine, ruleMap.get("args"));
        if (args != null && args.size() > 0) {
            log.info("- Arguments:");
            for (Object arg : args) {
                Map<?, ?> argMap = JSMap.toMap(engine, arg);
                log.info("  + {}: {}", argMap.get("name"), argMap.get("description"));
            }
        }
        // TODO commandMatchers
        if (ruleMap.containsKey("expandedCommands")) {
            log.info("- Expanded commands: array");
        } else if (ruleMap.containsKey("getExpandedCommands")) {
            log.info("- Expanded commands: function");
        } else {
            throw new SeleniumException("Missing expandedCommands nor getExpandedCommands in rollup rule definition.");
        }
    }
}
