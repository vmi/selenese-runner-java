package jp.vmi.selenium.selenese;

import org.openqa.selenium.internal.WrapsDriver;

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
}
