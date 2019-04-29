package jp.vmi.selenium.selenese.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

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
        if (Strings.isNullOrEmpty(s1))
            return s2;
        else
            return s1 + ", " + s2;
    }

    private static void addCommandInformationFromSubCommandMap(Map<String, String> commands) {
        try {
            SubCommandMap subCommandMap = new SubCommandMap();
            for (Entry<String, ISubCommand<?>> entry : subCommandMap.getMap().entrySet()) {
                String name = entry.getKey();
                ISubCommand<?> subCommand = entry.getValue();
                String info = "";
                info = append(info, "SR");
                Matcher matcher = GETTER.matcher(name);
                if (matcher.matches()) {
                    String getterInfo = append(info, "Generated from " + name);
                    String targetName = matcher.group(2);
                    int count = subCommand.getArgumentCount();
                    commands.put(String.format("assert%s(%d)", targetName, count + 1), getterInfo);
                    commands.put(String.format("verify%s(%d)", targetName, count + 1), getterInfo);
                    commands.put(String.format("waitFor%s(%d)", targetName, count + 1), getterInfo);
                    commands.put(String.format("store%s(%d)", targetName, count + 1), getterInfo);
                    if (targetName.endsWith("Present")) {
                        String notName = targetName.replaceFirst("Present$", "NotPresent");
                        commands.put(String.format("assert%s(%d)", notName, count), getterInfo);
                        commands.put(String.format("verify%s(%d)", notName, count), getterInfo);
                        commands.put(String.format("waitFor%s(%d)", notName, count), getterInfo);
                    } else {
                        commands.put(String.format("assertNot%s(%d)", targetName, count + 1), getterInfo);
                        commands.put(String.format("verifyNot%s(%d)", targetName, count + 1), getterInfo);
                        commands.put(String.format("waitForNot%s(%d)", targetName, count + 1), getterInfo);
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
            Constructor<? extends ICommand> ctr = entry.getValue();
            ICommand cmd;
            try {
                cmd = (ICommand) ctr.newInstance(-1, name);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                continue;
            }
            int count = cmd.getArgumentCount();
            String addInfo = commandInfo.containsKey(name) ? ",Override" : "";
            commandInfo.put(String.format("%s(%d)", name, count), info + addInfo);
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
            commandsJs.getCommands().forEach((name, cmd) -> {
                ICommand srCmd = null;
                int srACnt = 0;
                try {
                    srCmd = commandFactory.newCommand(-1, name);
                    srACnt = srCmd.getArgumentCount();
                } catch (SeleneseRunnerRuntimeException e) {
                    // no operation.
                }
                int aCnt = 0;
                if (cmd.get("target") != null)
                    aCnt++;
                if (cmd.get("value") != null)
                    aCnt++;
                String state;
                if (srCmd == null)
                    state = "--";
                else if (srACnt != aCnt)
                    state = "IC";
                else
                    state = "OK";

                System.out.printf("  [%s] %s (%d/%d)%n", state, name, srACnt, aCnt);
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
