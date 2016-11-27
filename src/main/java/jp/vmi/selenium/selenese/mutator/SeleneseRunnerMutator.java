package jp.vmi.selenium.selenese.mutator;

import jp.vmi.selenium.selenese.Context;
import jp.vmi.selenium.selenese.mutator.VariableDeclarationWithDynamicValue.DynamicValue;

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
            if (mutated.indexOf(BASE_URL) >= 0) {
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
