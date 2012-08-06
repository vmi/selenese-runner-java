package jp.vmi.selenium.selenese;

import java.io.File;
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

import jp.vmi.selenium.selenese.command.Command;
import jp.vmi.selenium.selenese.command.Command.Result;
import jp.vmi.selenium.selenese.command.DummyHead;
import jp.vmi.selenium.selenese.command.Label;
import jp.vmi.selenium.selenese.inject.DoCommand;
import jp.vmi.selenium.selenese.inject.ExecuteTestCase;
import jp.vmi.selenium.utils.LoggerUtils;

import static jp.vmi.selenium.selenese.command.Command.*;

public class TestCase implements Selenese {

    private static final Logger log = LoggerFactory.getLogger(TestCase.class);

    private File file;
    private String name;
    private String baseURI;
    private WebDriverCommandProcessor proc;
    private WebDriver driver;

    private final Map<String, String> variableMap = new HashMap<String, String>();
    private final Map<String, Deque<String>> collectionMap = new HashMap<String, Deque<String>>();
    private final Map<String, Label> labelCommandMap = new HashMap<String, Label>();

    private final Command head = new DummyHead();
    private Command prev = head;

    public TestCase initialize(File file, String name, WebDriver driver, String baseURI) {
        this.file = file;
        this.name = name;
        this.driver = driver;
        this.baseURI = baseURI;
        this.proc = new WebDriverCommandProcessor(baseURI, driver);
        return this;
    }

    public WebDriverCommandProcessor getProc() {
        return proc;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String doBuiltInCommand(String name, String... args) {
        try {
            return proc.doCommand(name, args);
        } catch (UnsupportedOperationException e) {
            throw new SeleniumException("No such command: " + name);
        } catch (SeleniumException e) {
            log.error("{}({})", name, StringUtils.join(LoggerUtils.quote(args), ", "));
            throw e;
        }
    }

    public boolean isBuiltInCommand(String name, String... args) {
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

    public void addCommand(Command command) {
        prev = prev.setNext(command);
    }

    @DoCommand
    public Result doCommand(Command command) {
        return command.doCommand(this);
    }

    @ExecuteTestCase
    @Override
    public Result execute(Runner runner) {
        log.info("baseURI: {}", baseURI);
        Command current = head.next(null);
        Result totalResult = SUCCESS;
        while (current != null) {
            log.info(current.toString());
            Result result = doCommand(current);
            runner.takeScreenshotAll(current.getIndex());
            totalResult = totalResult.update(result);
            if (totalResult.isInterrupted())
                break;
            current = current.next(this);
        }
        return totalResult;
    }

    @Override
    public String toString() {
        return "TestCase[" + name + "] (" + file + ")";
    }
}
