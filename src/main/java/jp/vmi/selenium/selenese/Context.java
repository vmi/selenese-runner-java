package jp.vmi.selenium.selenese;

import org.openqa.selenium.internal.WrapsDriver;

import jp.vmi.selenium.selenese.cmdproc.SeleneseRunnerCommandProcessor;

/**
 * Selenese Runner Context.
 */
public interface Context extends WrapsDriver {

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
     * Get SeleneseRunnerCommandProcessor instance.
     * 
     * @return SeleneseRunnerCommandProcessor instance.
     */
    SeleneseRunnerCommandProcessor getProc();
}
