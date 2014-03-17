package jp.vmi.selenium.rollup;

import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.script.JSArray;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.command.CommandList;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.command.ICommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;

/**
 * Rollup rule.
 */
public class RollupRule implements IRollupRule {

    private final ScriptEngine engine;
    private final Map<?, ?> rule;

    RollupRule(ScriptEngine engine, Map<?, ?> rule) {
        this.engine = engine;
        this.rule = rule;
    }

    @Override
    public String getName() {
        return (String) rule.get("name");
    }

    @Override
    public CommandList getExpandedCommands(Context context, Map<String, String> rollupArgs) {
        List<Map<String, String>> commands;
        if (rule.containsKey("expandedCommands")) {
            commands = JSArray.wrap(rule.get("expandedCommands"));
        } else {
            Bindings bindings = new SimpleBindings();
            bindings.put("rule", rule);
            String args = new JSONObject(rollupArgs).toString();
            try {
                commands = JSArray.wrap(engine.eval("rule.getExpandedCommands(" + args + ")", bindings));
            } catch (ScriptException e) {
                throw new SeleniumException(e);
            }
        }
        ICommandFactory factory = context.getCommandFactory();
        CommandList commandList = Binder.newCommandList();
        int index = 0;
        for (Map<String, String> c : commands) {
            String name = c.get("command");
            String target = StringUtils.defaultString(c.get("target"));
            String value = StringUtils.defaultString(c.get("value"));
            ICommand command = factory.newCommand(++index, name, target, value);
            commandList.add(command);
        }
        return commandList;
    }
}
