package jp.vmi.selenium.rollup;

import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.script.JSList;
import jp.vmi.script.JSMap;
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
        List<Object> commands;
        if (rule.containsKey("expandedCommands")) {
            commands = JSList.toList(engine, rule.get("expandedCommands"));
        } else {
            Bindings bindings = engine.createBindings();
            if (rule instanceof JSMap)
                bindings.put("rule", ((JSMap<?, ?>) rule).unwrap());
            else
                bindings.put("rule", rule);
            String args = new Gson().toJson(rollupArgs);
            try {
                commands = JSList.toList(engine, engine.eval("rule.getExpandedCommands(" + args + ")", bindings));
            } catch (ScriptException e) {
                throw new SeleniumException(e);
            }
        }
        ICommandFactory factory = context.getCommandFactory();
        CommandList commandList = Binder.newCommandList();
        int index = 0;
        for (Object o : commands) {
            Map<String, String> c = JSMap.toMap(engine, o);
            String name = c.get("command");
            String target = StringUtils.defaultString(c.get("target"));
            String value = StringUtils.defaultString(c.get("value"));
            ICommand command = factory.newCommand(++index, name, target, value);
            commandList.add(command);
        }
        return commandList;
    }
}
