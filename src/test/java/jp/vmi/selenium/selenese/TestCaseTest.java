package jp.vmi.selenium.selenese;

import org.junit.Before;
import org.junit.Test;

import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.junit.Assert.*;

/**
 * Test for {@link TestCase}.
 */
public class TestCaseTest {

    private final Runner runner = new Runner();

    /**
     * Initialize.
     */
    @Before
    public void initialize() {
        WebDriverManager manager = WebDriverManager.getInstance();
        manager.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        runner.setDriver(manager.get());
    }

    /**
     * Test of replaceVariables(String).
     */
    @Test
    public void replaceVariable() {
        TestCase c = Binder.newTestCase(null, null, runner, "");
        c.setVariable("XYZ", "a");
        assertEquals("XYZ", c.replaceVariables("${a}"));
    }

    /**
     * Test of replaceVariables(String[]).
     */
    @Test
    public void replaceVariables() {
        TestCase c = Binder.newTestCase(null, null, runner, "");
        c.setVariable("XYZ", "a");
        assertArrayEquals(new String[] { "abc", "XYZ", "abcXYZbca" }, c.replaceVariables(new String[] { "abc", "${a}", "abc${a}bca" }));
    }
}
