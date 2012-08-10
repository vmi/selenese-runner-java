package jp.vmi.selenium.selenese.command;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * test for {@link Assertion}.
 */
public class AssertionTest {

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
        testcase.initialize(selenesefile, "test", wdm.get(), "");

        Result result = assertion.doCommand(testcase);

        assertThat(result.getMessage(), is("Assertion failed (Result: [] / Expected: [title]"));
    }
}
