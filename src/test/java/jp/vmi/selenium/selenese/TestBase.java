package jp.vmi.selenium.selenese;

import org.junit.Rule;

import jp.vmi.selenium.testutils.WebServerResouce;

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
