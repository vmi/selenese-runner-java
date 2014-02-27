package jp.vmi.selenium.selenese.command;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import jp.vmi.selenium.selenese.Eval;

/**
 * An implementation of the "runScript" method from Selenium.
 */
public class RunScript extends SeleneseCommand<Void> {

    private final Eval eval;

    /**
     * Constructor.
     *
     * @param eval evaluator.
     */
    public RunScript(Eval eval) {
        this.eval = eval;
    }

    @Override
    protected Void handleSeleneseCommand(WebDriver driver, String script, String ignored) {
        eval.eval(driver, script);
        return null;
    }
}
