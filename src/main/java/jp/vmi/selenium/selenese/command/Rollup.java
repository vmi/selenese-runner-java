package jp.vmi.selenium.selenese.command;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

import jp.vmi.selenium.rollup.IRollupRule;
import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.result.CommandResultList;
import jp.vmi.selenium.selenese.result.Error;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.selenese.result.Success;

import static jp.vmi.selenium.selenese.command.ArgumentType.*;
import static jp.vmi.selenium.selenese.result.Success.*;

/**
 * Command "rollup".
 */
public class Rollup extends AbstractCommand {

    private static final int ARG_ROLLUP_NAME = 0;
    private static final int ARG_KWARGS = 1;

    // requirement: kwArgs is trim'ed.
    private static final Pattern RE_KW_ARGS = Pattern.compile("\\G(\\w+)\\s*=\\s*(?:" // 1
        + "\"((?:\\\\.|[^\\\"])*)\"" // 2
        + "|'((?:\\\\.|[^\\'])*)'" // 3
        + "|(.*?)" // 4
        + ")(?:\\s*,\\s*|\\z)");

    // matcher groups.
    private static final int MG_NAME = 1;
    private static final int MG_DOUBLE_QUOTE = 2;
    private static final int MG_SINGLE_QUOTE = 3;
    private static final int MG_NO_QUOTE = 4;

    Rollup(int index, String name, String... args) {
        super(index, name, args, VALUE, VALUE);
    }

    private Map<String, String> parseKwArgs(String kwArgs) {
        Map<String, String> map = new HashMap<String, String>();
        Matcher matcher = RE_KW_ARGS.matcher(kwArgs.trim());
        while (matcher.find()) {
            String name = matcher.group(MG_NAME);
            String value = matcher.group(MG_DOUBLE_QUOTE);
            if (value == null)
                value = matcher.group(MG_SINGLE_QUOTE);
            if (value == null)
                value = matcher.group(MG_NO_QUOTE);
            else
                value = StringEscapeUtils.unescapeEcmaScript(value); // if quoted
            map.put(name, value);
        }
        return map;
    }

    @Override
    protected Result executeImpl(Context context, String... curArgs) {
        String rollupName = curArgs[ARG_ROLLUP_NAME];
        String kwArgs = curArgs[ARG_KWARGS].trim();
        Map<String, String> kwArgsMap = parseKwArgs(kwArgs);
        IRollupRule rollupRule = context.getRollupRules().get(rollupName);
        if (rollupRule == null)
            return new Error("No such rollup rule: " + rollupName);
        CommandList commandList = rollupRule.getExpandedCommands(context, kwArgsMap);
        Result result = commandList.execute(context, new CommandResultList());
        return result == SUCCESS ? new Success("Success: " + rollupRule.getName()) : result;
    }
}
