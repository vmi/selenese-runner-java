package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class RunScript extends SeleneseCommand<Void> {

    private final Eval eval;

    public RunScript(Eval eval) {
        this.eval = eval;
    }

    @Override
    protected Void handleSeleneseCommand(WebDriver driver, String script, String ignored) {
        eval.eval(driver, script);
        return null;
    }
}
