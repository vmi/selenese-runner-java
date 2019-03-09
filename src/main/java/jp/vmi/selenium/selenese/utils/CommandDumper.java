package jp.vmi.selenium.selenese.utils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.selenium.runner.model.utils.CommandsJs;
import jp.vmi.selenium.selenese.SeleneseRunnerRuntimeException;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.command.ICommand;
import jp.vmi.selenium.selenese.subcommand.ISubCommand;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;

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
            SubCommandMap subCommandMap = new SubCommandMap();
            for (Entry<String, ISubCommand<?>> entry : subCommandMap.getMap().entrySet()) {
                String name = entry.getKey();
                String info = "";
                info = append(info, "SR");
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

    private static void showValue(Map<String, String> map, String key) {
        String value = map.get(key);
        if (value == null)
            return;
        System.out.printf("- %s: %s%n", key, value);
    }

    /**
     * main.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Map<String, String> commandInfo = new HashMap<>();
            addCommandInformationFromSubCommandMap(commandInfo);
            addCommandInformationFromCommandFactory(commandInfo);
            commandInfo.entrySet().stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .forEach(entry -> System.out.println(entry.getKey() + "," + entry.getValue()));
        } else if ("--side".equals(args[0])) {
            CommandFactory commandFactory = new CommandFactory();
            CommandsJs commandsJs = CommandsJs.load();
            System.out.println("* Supported status of Selenium IDE commands:");
            commandsJs.getCommands().keySet().forEach(name -> {
                boolean exists = true;
                try {
                    commandFactory.newCommand(-1, name);
                } catch (SeleneseRunnerRuntimeException e) {
                    exists = false;
                }
                System.out.printf("  [%s] %s%n", exists ? "OK" : "--", name);
            });
        } else if ("--side-commands-info".equals(args[0])) {
            CommandsJs commandsJs = CommandsJs.load();
            System.out.println("* Information of Selenium IDE commands:");
            System.out.println();
            commandsJs.getCommands().forEach((name, info) -> {
                System.out.printf("[%s] - %s%n", name, info.get("name"));
                showValue(info, "type");
                showValue(info, "description");
                showValue(info, "target");
                showValue(info, "value");
                System.out.println();
            });
        }
    }
}
