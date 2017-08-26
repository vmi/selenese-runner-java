package jp.vmi.selenium.selenese.mutator;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

import jp.vmi.selenium.selenese.Context;

/**
 * Variable declaration with dynamic value.
 */
public class VariableDeclarationWithDynamicValue implements ScriptMutator {

    private final Pattern pattern;
    private final String declaration;
    private final Function<Context, String> dynamicValue;

    /**
     * Constructor.
     *
     * @param varName variable name.
     * @param dynamicValue value getter on demand.
     */
    public VariableDeclarationWithDynamicValue(String varName, Function<Context, String> dynamicValue) {
        pattern = MutatorUtils.generatePatternForCodePresence(varName);
        if (varName.indexOf('.') < 0)
            declaration = "var " + varName + " = '";
        else
            declaration = varName + " = '";
        this.dynamicValue = dynamicValue;
    }

    @Override
    public void mutate(Context context, String script, StringBuilder outputTo) {
        if (pattern.matcher(script).find()) {
            String value = dynamicValue.apply(context);
            outputTo.append(declaration).append(StringEscapeUtils.escapeEcmaScript(value)).append("';");
        }
    }
}
