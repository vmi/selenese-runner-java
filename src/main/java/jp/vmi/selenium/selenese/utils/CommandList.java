package jp.vmi.selenium.selenese.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.seleniumemulation.NoOp;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.command.CommandFactory;

/**
 * List supported commands.
 */
public class CommandList {

    private static final Pattern GETTER = Pattern.compile("(get|is)([A-Z].*)");

    private static class DummyDriver implements WebDriver, JavascriptExecutor {

        @Override
        public void get(String url) {
        }

        @Override
        public String getCurrentUrl() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public List<WebElement> findElements(By by) {
            return null;
        }

        @Override
        public WebElement findElement(By by) {
            return null;
        }

        @Override
        public String getPageSource() {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public void quit() {

        }

        @Override
        public Set<String> getWindowHandles() {
            return null;
        }

        @Override
        public String getWindowHandle() {
            return null;
        }

        @Override
        public TargetLocator switchTo() {
            return null;
        }

        @Override
        public Navigation navigate() {
            return null;
        }

        @Override
        public Options manage() {
            return null;
        }

        @Override
        public Object executeScript(String script, Object... args) {
            return null;
        }

        @Override
        public Object executeAsyncScript(String script, Object... args) {
            return null;
        }
    }

    private static void extractCommandsFromCommandProcessor(Collection<String> commands) {
        try {
            SeleneseRunnerCommandProcessor proc = new SeleneseRunnerCommandProcessor("", new DummyDriver(),
                new HashMap<String, Object>());
            Field methodsField = WebDriverCommandProcessor.class.getDeclaredField("seleneseMethods");
            methodsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, SeleneseCommand<?>> methodsMap = (Map<String, SeleneseCommand<?>>) methodsField.get(proc);
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
