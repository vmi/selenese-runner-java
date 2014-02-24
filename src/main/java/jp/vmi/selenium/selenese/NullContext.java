package jp.vmi.selenium.selenese;

import java.io.PrintStream;

import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.cmdproc.Eval;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Null implementation of Context.
 */
public class NullContext implements Context {

    @Override
    public PrintStream getPrintStream() {
        return null;
    }

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
    public boolean isTrue(String expr) {
        return false;
    }

    @Override
    public SeleneseRunnerCommandProcessor getProc() {
        return null;
    }

    @Override
    public void setDefaultBaseURL(String defaultBaseURL) {
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public void resetSpeed() {
    }

    @Override
    public void setSpeed(long speed) {
    }

    @Override
    public void waitSpeed() {
    }
}
