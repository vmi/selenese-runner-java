package jp.vmi.selenium.selenese;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.utils.LoggerUtils;

public class Context {

    private static final Logger log = LoggerFactory.getLogger(Context.class);

    private final WebDriverCommandProcessor proc;
    private final WebDriver driver;

    private final Map<String, String> variableMap = new HashMap<String, String>();
    private final Map<String, Deque<String>> collectionMap = new HashMap<String, Deque<String>>();
    private final Map<String, Label> labelCommandMap = new HashMap<String, Label>();

    public Context(WebDriverCommandProcessor proc, WebDriver driver) {
        this.proc = proc;
        this.driver = driver;
    }

    public String doCommand(String name, String... args) {
        try {
            return proc.doCommand(name, args);
        } catch (UnsupportedOperationException e) {
            throw new SeleniumException("No such command: " + name);
        } catch (SeleniumException e) {
            log.error("{}({})", name, StringUtils.join(LoggerUtils.quote(args), ", "));
            throw e;
        }
    }

    public boolean isCommand(String name, String... args) {
        try {
            return proc.getBoolean(name, args);
        } catch (UnsupportedOperationException e) {
            throw new SeleniumException("No such command: " + name);
        } catch (SeleniumException e) {
            log.error("{}({})", name, StringUtils.join(LoggerUtils.quote(args), ", "));
            throw e;
        }
    }

    public void setVariable(String value, String varName) {
        variableMap.put(varName, value);
    }

    public String replaceVariables(String expr) {
        StrSubstitutor s = new StrSubstitutor(variableMap);
        return s.replace(expr);
    }

    public String[] replaceVariables(String[] exprs) {
        String[] result = new String[exprs.length];
        for (int i = 0; i < exprs.length; i++)
            result[i] = replaceVariables(exprs[i]);
        return result;
    }

    public boolean isTrue(String expr) {
        return Boolean.parseBoolean(proc.doCommand("getEval", new String[] { replaceVariables(expr) }));
    }

    public void addCollection(String collectionName) {
        collectionMap.put(collectionName, new ArrayDeque<String>());
    }

    public void addToCollection(String collectionName, String value) {
        Deque<String> collection = collectionMap.get(collectionName);
        collection.addLast(value);
    }

    public String pollFromCollection(String collectionName) {
        Deque<String> collection = collectionMap.get(collectionName);
        return collection.pollFirst();
    }

    public void setLabelCommand(Label labelCommand) {
        labelCommandMap.put(labelCommand.getLabel(), labelCommand);
    }

    public Label getLabelCommand(String label) {
        return labelCommandMap.get(label);
    }

    public WebDriverCommandProcessor getProc() {
        return proc;
    }

    public WebDriver getDriver() {
        return driver;
    }

}
