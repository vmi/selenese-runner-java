package jp.vmi.selenium.selenese;

import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.cmdproc.Eval;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Null implementation of Context.
 */
public class NullContext implements Context {

    @Override
    public WebDriver getWrappedDriver() {
        return null;
    }

    @Override
    public String getCurrentBaseURL() {
        return null;
    }

    @Override
    public VarsMap getVarsMap() {
        return null;
    }

    @Override
    public String getInitialWindowHandle() {
        return null;
    }

    @Override
    public WebDriverElementFinder getElementFinder() {
        return null;
    }

    @Override
    public Eval getEval() {
        return null;
    }

    @Override
    public SeleneseRunnerCommandProcessor getProc() {
        return null;
    }

    public void setDefaultBaseURL(String defaultBaseURL) {
    }
}
