package jp.vmi.selenium.selenese.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.internal.seleniumemulation.NoOp;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import jp.vmi.selenium.selenese.NullContext;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.subcommand.SubCommandMap;

/**
 * List supported commands.
 */
public class CommandList {

    private static final Pattern GETTER = Pattern.compile("(get|is)([A-Z].*)");

    private static void extractCommandsFromCommandProcessor(Collection<String> commands) {
        try {
            SubCommandMap subCommandMap = new SubCommandMap(new NullContext());
            Field methodsField = WebDriverCommandProcessor.class.getDeclaredField("seleneseMethods");
            methodsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, SeleneseCommand<?>> methodsMap = (Map<String, SeleneseCommand<?>>) methodsField.get(subCommandMap);
            Set<String> methods = methodsMap.keySet();
            for (String method : methods) {
                if (methodsMap.get(method) instanceof NoOp)
                    continue;
                Matcher matcher = GETTER.matcher(method);
                if (matcher.matches()) {
                    String name = matcher.group(2);
                    commands.add("assert" + name);
                    commands.add("verify" + name);
                    commands.add("waitFor" + name);
                    commands.add("store" + name);
                    if (name.endsWith("Present")) {
                        String notName = name.replaceFirst("Present$", "NotPresent");
                        commands.add("assert" + notName);
                        commands.add("verify" + notName);
                        commands.add("waitFor" + notName);
                    } else {
                        commands.add("assertNot" + name);
                        commands.add("verifyNot" + name);
                        commands.add("waitForNot" + name);
                    }
                } else {
                    commands.add(method);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * main.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        Set<String> commandNames = new HashSet<String>();
        extractCommandsFromCommandProcessor(commandNames);
        CommandFactory.addCommandNames(commandNames);
        List<String> result = new ArrayList<String>(commandNames);
        Collections.sort(result);
        for (String name : result)
            System.out.println(name);
    }
}
