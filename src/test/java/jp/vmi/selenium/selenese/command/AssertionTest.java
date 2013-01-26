package jp.vmi.selenium.selenese.command;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestBase;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestUtils;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * test for {@link Assertion}.
 */
public class AssertionTest extends TestBase {

    /**
     * test of user friendly assertion message.
     *
     * @throws IOException exception.
     */
    @Test
    public void userFriendlyAssertionMessage() throws IOException {
        Assertion assertion = new Assertion(1, "assertTitle", new String[] { "title", "title" }, "assert", "getTitle", false, false);

        File selenesefile = File.createTempFile("selenese", ".html");

        TestCase testcase = new TestCase();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        Runner runner = new Runner();
        runner.setDriver(wdm.get());
        testcase.initialize(selenesefile, "test", runner, ws.getUrl());

        Result result = assertion.doCommand(testcase);

        assertThat(result.getMessage(), is("Failure: Assertion failed (Result: [] / Expected: [title])"));
    }

    /**
     * test for string-match
     *
     * @see "http://release.seleniumhq.org/selenium-core/1.0.1/reference.html#patterns"
     *
     * @throws IOException exception.
     */
    @Test
    public void stringMatchPattern() throws IOException {
        Command open = new BuiltInCommand(1, "open", new String[] { "file://" + TestUtils.getScriptFile(AssertionTest.class, "") },
            "open", true);
        Assertion assertionDefaultGlob = new Assertion(1, "assertTitle", new String[] { "ti*" }, "assert", "getTitle", false, false);
        Assertion assertionGlob = new Assertion(2, "assertTitle", new String[] { "glob:ti*" }, "assert", "getTitle", false, false);
        Assertion assertionRegexp = new Assertion(3, "assertTitle", new String[] { "regexp:ti.+" }, "assert", "getTitle", false, false);
        Assertion assertionRegexpi = new Assertion(4, "assertTitle", new String[] { "regexpi:TI.+" }, "assert", "getTitle", false,
            false);
        Assertion assertionExact = new Assertion(5, "assertTitle", new String[] { "exact:title" }, "assert", "getTitle", false, false);

        Assertion assertionFailDefaultGlob = new Assertion(1, "assertTitle", new String[] { "ti*l" }, "assert", "getTitle", false,
            false);
        Assertion assertionFailGlob = new Assertion(2, "assertTitle", new String[] { "glob:ti*l" }, "assert", "getTitle", false, false);
        Assertion assertionFailRegexp = new Assertion(3, "assertTitle", new String[] { "regexp:ti.+l$" }, "assert", "getTitle", false,
            false);
        Assertion assertionFailRegexpi = new Assertion(4, "assertTitle", new String[] { "regexpi:TI.+l$" }, "assert", "getTitle",
            false,
            false);
        Assertion assertionFailExact = new Assertion(5, "assertTitle", new String[] { "exact:titl" }, "assert", "getTitle", false,
            false);
        Assertion assertionNotDefaultGlob = new Assertion(1, "assertNotTitle", new String[] { "ti*" }, "assert", "getTitle", false,
            true);
        Assertion assertionNotGlob = new Assertion(2, "assertNotTitle", new String[] { "glob:ti*" }, "assert", "getTitle", false, true);
        Assertion assertionNotRegexp = new Assertion(3, "assertNotTitle", new String[] { "regexp:ti.+" }, "assert", "getTitle", false,
            true);
        Assertion assertionNotRegexpi = new Assertion(4, "assertNotTitle", new String[] { "regexpi:TI.+" }, "assert", "getTitle",
            false,
            true);
        Assertion assertionNotExact = new Assertion(5, "assertNotTitle", new String[] { "exact:title" }, "assert", "getTitle", false,
            true);

        Assertion issue56 = new Assertion(6, "assertText", new String[] { "test", "something*" }, "assert", "getText", false,
            false);

        File selenesefile = File.createTempFile("selenese", ".html");

        TestCase testcase = new TestCase();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        Runner runner = new Runner();
        runner.setDriver(wdm.get());
        testcase.initialize(selenesefile, "test", runner, ws.getUrl());

        assertTrue(open.doCommand(testcase).isSuccess());

        assertTrue(assertionDefaultGlob.doCommand(testcase).getMessage(), assertionDefaultGlob.doCommand(testcase).isSuccess());
        assertTrue(assertionGlob.doCommand(testcase).getMessage(), assertionGlob.doCommand(testcase).isSuccess());
        assertTrue(assertionRegexp.doCommand(testcase).getMessage(), assertionRegexp.doCommand(testcase).isSuccess());
        assertTrue(assertionRegexpi.doCommand(testcase).getMessage(), assertionRegexpi.doCommand(testcase).isSuccess());
        assertTrue(assertionExact.doCommand(testcase).getMessage(), assertionExact.doCommand(testcase).isSuccess());

        assertTrue(assertionFailDefaultGlob.doCommand(testcase).getMessage(), assertionFailDefaultGlob.doCommand(testcase).isFailed());
        assertTrue(assertionFailGlob.doCommand(testcase).getMessage(), assertionFailGlob.doCommand(testcase).isFailed());
        assertTrue(assertionFailRegexp.doCommand(testcase).getMessage(), assertionFailRegexp.doCommand(testcase).isFailed());
        assertTrue(assertionFailRegexpi.doCommand(testcase).getMessage(), assertionFailRegexpi.doCommand(testcase).isFailed());
        assertTrue(assertionFailExact.doCommand(testcase).getMessage(), assertionFailExact.doCommand(testcase).isFailed());

        assertTrue(assertionNotDefaultGlob.doCommand(testcase).getMessage(), assertionNotDefaultGlob.doCommand(testcase).isFailed());
        assertTrue(assertionNotGlob.doCommand(testcase).getMessage(), assertionNotGlob.doCommand(testcase).isFailed());
        assertTrue(assertionNotRegexp.doCommand(testcase).getMessage(), assertionNotRegexp.doCommand(testcase).isFailed());
        assertTrue(assertionNotRegexpi.doCommand(testcase).getMessage(), assertionNotRegexpi.doCommand(testcase).isFailed());
        assertTrue(assertionNotExact.doCommand(testcase).getMessage(), assertionNotExact.doCommand(testcase).isFailed());

        assertTrue(issue56.doCommand(testcase).getMessage(), issue56.doCommand(testcase).isSuccess());
    }
}
