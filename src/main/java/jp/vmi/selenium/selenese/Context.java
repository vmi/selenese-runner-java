package jp.vmi.selenium.selenese;

import java.io.PrintStream;

import org.openqa.selenium.internal.WrapsDriver;

import jp.vmi.selenium.selenese.cmdproc.Eval;
import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;
import jp.vmi.selenium.selenese.locator.WebDriverElementFinder;

/**
 * Selenese Runner Context.
 */
public interface Context extends WrapsDriver {

    /**
     * Get PrintStream for logging.
     *
     * @return PrintStram object.
     */
    PrintStream getPrintStream();

    /**
     * Set default base URL.
     *
     * @param defaultBaseURL base URL in test-case.
     */
    void setDefaultBaseURL(String defaultBaseURL);

    /**
     * Get current base URL.
     *
     * @return base URL.
     */
    String getCurrentBaseURL();

    /**
     * Get variables map.
     *
     * @return varsMap.
     */
    VarsMap getVarsMap();

    /**
     * Get initial window handle.
     *
     * @return window handle.
     */
    String getInitialWindowHandle();

    /**
     * Get elemnt finder.
     *
     * @return element finder.
     */
    WebDriverElementFinder getElementFinder();

    /**
     * Get evaluater.
     *
     * @return eval object.
     */
    Eval getEval();

    /**
     * Get boolean value of expr.
     * 
     * @param expr expression.
     * @return cast from result of expr to Javascript Boolean.
     */
    boolean isTrue(String expr);

    /**
     * Get SeleneseRunnerCommandProcessor instance.
     * 
     * @return SeleneseRunnerCommandProcessor instance.
     */
    SeleneseRunnerCommandProcessor getProc();

    /**
     * Get timeout for waiting. (ms)
     *
     * @return timeout for waiting.
     */
    public int getTimeout();

    /**
     * Reset speed as initial speed.
     */
    void resetSpeed();

    /**
     * Get speed for setSpeed command.
     *
     * @return speed.
     */
    long getSpeed();

    /**
     * Set speed for setSpeed command.
     *
     * @param speed speed.
     */
    void setSpeed(long speed);

    /**
     * Wait according to speed setting. 
     */
    void waitSpeed();
}
