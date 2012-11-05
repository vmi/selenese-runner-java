package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class GetEval extends org.openqa.selenium.internal.seleniumemulation.GetEval {

    private final Eval eval;

    /**
     * Constructor.
     *
     * @param eval evaluator.
     */
    public GetEval(Eval eval) {
        super(null);
        this.eval = eval;
    }

    @Override
    protected String handleSeleneseCommand(WebDriver driver, String script, String ignored) {
        Object result = eval.eval(driver, script);
        return result == null ? "" : result.toString();
    }
}
