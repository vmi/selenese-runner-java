package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class GetEval extends SeleneseCommand<Object> {

    private final Eval eval;

    /**
     * Constructor.
     *
     * @param eval evaluator.
     */
    public GetEval(Eval eval) {
        this.eval = eval;
    }

    @Override
    protected Object handleSeleneseCommand(WebDriver driver, String script, String ignored) {
        return eval.eval(driver, script);
    }
}
