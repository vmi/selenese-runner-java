package jp.vmi.selenium.selenese.javascript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Javascript Function.
 */
public class JSFunction {

    private final String function;

    private JSFunction(String function) {
        this.function = function;
    }

    private static final Pattern BEGIN_RE = Pattern.compile("function\\s+(?<name>\\w+)\\((?<args>.*?)\\).*");
    private static final Pattern END_RE = Pattern.compile("\\}.*");

    /**
     * Load Javascript file.
     *
     * @param is InputStream object.
     * @return JSFunction
     */
    public static Map<String, JSFunction> load(InputStream is) {
        Map<String, JSFunction> functions = new HashMap<>();
        String line;
        String name = null;
        boolean hasArgs = false;
        StringBuilder body = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
                if (name == null) {
                    Matcher matcher = BEGIN_RE.matcher(line);
                    if (matcher.matches()) {
                        name = matcher.group("name");
                        String args = matcher.group("args");
                        if (hasArgs = !args.isEmpty())
                            body = new StringBuilder("return (function(" + args + "){");
                        else
                            body = new StringBuilder();
                    }
                } else {
                    if (END_RE.matcher(line).matches()) {
                        if (hasArgs)
                            body.append("}).apply(null, arguments)");
                        functions.put(name, new JSFunction(body.toString()));
                        name = null;
                        body = null;
                        hasArgs = false;
                    } else {
                        body.append(line.trim()).append('\n');
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return functions;
    }

    /**
     * Load Javascript file as a function.
     *
     * @param is InputStream object.
     * @return JSFunction
     */
    public static JSFunction loadFunction(InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String body = br.lines().collect(Collectors.joining("\n", "return (", ").apply(null, arguments);"));
            return new JSFunction(body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Call Javascript function.
     *
     * @param driver WebDriver object.
     * @param args arguments.
     * @param <T> type of function result.
     * @return function result.
     */
    public <T> T call(WebDriver driver, Object... args) {
        @SuppressWarnings("unchecked")
        T result = (T) ((JavascriptExecutor) driver).executeScript(function, args);
        return result;
    }
}
