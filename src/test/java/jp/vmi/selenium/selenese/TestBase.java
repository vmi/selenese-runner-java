package jp.vmi.selenium.selenese;

import org.junit.Rule;

/**
 * Base class for test.
 */
public class TestBase {

    /**
     * Webserver for test
     */
    @Rule
    public final WebServerResouce ws = new WebServerResouce();
}
