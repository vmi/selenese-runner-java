package jp.vmi.selenium.selenese.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.selenese.NullContext;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;
import jp.vmi.selenium.selenese.subcommand.WDCommand;

/**
 * List supported commands.
 */
public class CommandDumper {

    private static final Pattern GETTER = Pattern.compile("(get|is)([A-Z].*)");

    private CommandDumper() {
    }

    private static String append(String s1, String s2) {
        if (StringUtils.isEmpty(s1))
            return s2;
        else
            return s1 + ", " + s2;
    }

    private static void addCommandInformationFromSubCommandMap(Map<String, String> commands) {
        try {
            SubCommandMap subCommandMap = new SubCommandMap(new NullContext());
            for (Entry<String, ISubCommand<?>> entry : subCommandMap.getMap().entrySet()) {
                String name = entry.getKey();
                ISubCommand<?> command = entry.getValue();
                String info = "";
                if (command instanceof WDCommand) {
                    info = append(info, "WD");
                    if (((WDCommand) command).isNoOp())
                        info = append(info, "NOP");
                } else {
                    info = append(info, "SR");
                }
                Matcher matcher = GETTER.matcher(name);
                if (matcher.matches()) {
                    String getterInfo = append(info, "Generated from " + name);
                    String targetName = matcher.group(2);
                    commands.put("assert" + targetName, getterInfo);
                    commands.put("verify" + targetName, getterInfo);
                    commands.put("waitFor" + targetName, getterInfo);
                    commands.put("store" + targetName, getterInfo);
                    if (targetName.endsWith("Present")) {
                        String notName = targetName.replaceFirst("Present$", "NotPresent");
                        commands.put("assert" + notName, getterInfo);
                        commands.put("verify" + notName, getterInfo);
                        commands.put("waitFor" + notName, getterInfo);
                    } else {
                        commands.put("assertNot" + targetName, getterInfo);
                        commands.put("verifyNot" + targetName, getterInfo);
                        commands.put("waitForNot" + targetName, getterInfo);
                    }
                } else {
                    commands.put(name, info);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static void addCommandInformationFromCommandFactory(Map<String, String> commandInfo) {
        String info = "SR";
        for (Entry<String, Constructor<? extends ICommand>> entry : CommandFactory.getCommandEntries()) {
            String name = entry.getKey();
            String addInfo = commandInfo.containsKey(name) ? ",Override" : "";
            commandInfo.put(name, info + addInfo);
        }
        commandInfo.put("store", info); // rewrite storeExpression
    }

    /**
     * main.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        Map<String, String> commandInfo = new HashMap<>();
        addCommandInformationFromSubCommandMap(commandInfo);
        addCommandInformationFromCommandFactory(commandInfo);
        List<Entry<String, String>> result = new ArrayList<>(commandInfo.entrySet());
        Collections.sort(result, new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> e1, Entry<String, String> e2) {
                return e1.getKey().compareTo(e2.getKey());
            }
        });
        for (Entry<String, String> entry : result)
            System.out.println(entry.getKey() + "," + entry.getValue());
    }
}
