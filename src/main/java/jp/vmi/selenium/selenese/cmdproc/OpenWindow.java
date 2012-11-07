package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

/**
 * An implementation of the "openWindow" method from Selenium.
 */
public class OpenWindow extends SeleneseCommand<Void> {

    private final Eval eval;

    /**
     * Constructor.
     *
     * @param eval evaluator.
     */
    public OpenWindow(Eval eval) {
        this.eval = eval;
    }

    @Override
    protected Void handleSeleneseCommand(WebDriver driver, String url, String windowID) {
        String script = String.format("window.open('%s', '%s');", url, windowID);
        eval.eval(driver, script);
        return null;
    }
}
