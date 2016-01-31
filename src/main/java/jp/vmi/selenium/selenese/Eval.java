package jp.vmi.selenium.selenese;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.google.gson.Gson;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Evaluator of script including "storedVars" variable.
 */
public class Eval {

    private final SeleneseRunnerMutator mutator;
    private final Context context;

    /**
     * Constructor.
     *
     * @param context Selenese Runner context.
     */
    public Eval(Context context) {
        this.mutator = new SeleneseRunnerMutator(context);
        this.context = context;
    }

    /**
     * Evaluate script including "storedVars" variable.
     *
     * @param driver WebDriver instance.
     * @param script JavaScript code.
     * @return result of evaluating script.
     */
    public Object eval(WebDriver driver, String script) {
        return eval(driver, script, null);
    }

    /**
     * Evaluate script including "storedVars" variable.
     *
     * @param driver WebDriver instance.
     * @param script JavaScript code.
     * @param cast cast type.
     * @return result of evaluating script.
     */
    public Object eval(WebDriver driver, String script, String cast) {
        VarsMap varsMap = context.getVarsMap();
        boolean hasStoredVars = script.matches(".*\\bstoredVars\\b.*");
        StringBuilder writer = new StringBuilder();
        if (hasStoredVars) {
            writer.append("return (function(){var storedVars = ");
            new Gson().toJson(varsMap, writer);
            writer.append(";\n");
        }
        writer.append("return [");
        if (cast != null)
            writer.append(cast);
        writer.append("((function(){");
        mutator.mutate(script, writer);
        writer.append("})())");
        if (hasStoredVars)
            writer.append(", storedVars];})();");
        else
            writer.append("];");
        Object result = ((JavascriptExecutor) driver).executeScript(writer.toString());
        if (!(result instanceof List))
            throw new SeleniumException(result.toString());
        List<?> list = (List<?>) result;
        switch (list.size()) {
        case 0:
            return null;
        case 1:
            return list.get(0);
        default: // case 2:
            @SuppressWarnings("unchecked")
            Map<String, Object> newVarsMap = (Map<String, Object>) list.get(1);
            varsMap.clear();
            varsMap.putAll(newVarsMap);
            return list.get(0);
        }
    }
}
