package jp.vmi.selenium.selenese.cmdproc;

import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.internal.seleniumemulation.ScriptMutator;

/**
 * Variable declaration with dynamic value.
 */
public class VariableDeclarationWithDynamicValue implements ScriptMutator {

    @SuppressWarnings("javadoc")
    public static interface DynamicValue {
        String getValue();
    }

    private final String varName;
    private final DynamicValue dynamicValue;

    /**
     * Constructor.
     *
     * @param varName variable name.
     * @param dynamicValue value getter on demand.
     */
    public VariableDeclarationWithDynamicValue(String varName, DynamicValue dynamicValue) {
        this.varName = varName;
        this.dynamicValue = dynamicValue;
    }

    @Override
    public void mutate(String script, StringBuilder outputTo) {
        if (script.contains(varName)) {
            String value = dynamicValue.getValue();
            outputTo.append(varName + " = '" + StringEscapeUtils.escapeEcmaScript(value) + "';");
        }
    }
}
