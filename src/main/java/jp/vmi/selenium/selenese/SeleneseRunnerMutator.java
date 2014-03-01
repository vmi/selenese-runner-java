package jp.vmi.selenium.selenese;

import com.thoughtworks.selenium.webdriven.CompoundMutator;
import com.thoughtworks.selenium.webdriven.ScriptMutator;
import com.thoughtworks.selenium.webdriven.VariableDeclaration;

import jp.vmi.selenium.selenese.VariableDeclarationWithDynamicValue.DynamicValue;

/**
 * Substitute for CompoundMutator without static base URL.
 */
public class SeleneseRunnerMutator extends CompoundMutator implements ScriptMutator {

    private static final String BASE_URL = "selenium.browserbot.baseUrl";

    private final Context context;

    /**
     * Constructor.
     *
     * @param context Selenese Runner context.
     */
    public SeleneseRunnerMutator(Context context) {
        super("");
        this.context = context;
    }

    @Override
    public void addMutator(ScriptMutator mutator) {
        if (mutator instanceof VariableDeclaration) {
            StringBuilder mutated = new StringBuilder();
            mutator.mutate(BASE_URL, mutated);
            if (mutated.length() > 0) {
                mutator = new VariableDeclarationWithDynamicValue(BASE_URL, new DynamicValue() {
                    @Override
                    public String getValue() {
                        return context.getCurrentBaseURL();
                    }
                });
            }
        }
        super.addMutator(mutator);
    }
}
