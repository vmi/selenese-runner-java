package jp.vmi.selenium.selenese.cmdproc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.internal.seleniumemulation.SeleneseCommand;

import com.thoughtworks.selenium.Wait;

/**
 * An implementation of the "getEval" method from Selenium.
 */
public class WaitForCondition extends SeleneseCommand<Void> {

    private final Eval eval;

    public WaitForCondition(Eval eval) {
        this.eval = eval;
    }

    @Override
    protected Void handleSeleneseCommand(final WebDriver driver, final String script, final String timeout) {
        new Wait() {
            @Override
            public boolean until() {
                Object result = eval.eval(driver, script);
                if (result == null) {
                    return false;
                } else if (result instanceof String) {
                    return !"".equals(result);
                } else if (result instanceof Boolean) {
                    return (Boolean) result;
                } else {
                    return true;
                }
            }
        }.wait("Failed to resolve " + script, Long.valueOf(timeout));
        return null;
    }
}
