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
 * Test class for BuiltInCommand class.
 */
public class BuiltInCommandTest {
    @Test
    public void userfriendryErrorMessage() throws IOException {
        Command click = new BuiltInCommand(1, "click", new String[] { "link=linktext" }, "click", false);

        File selenesefile = File.createTempFile("selenese", ".html");

        TestCase testcase = new TestCase();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(WebDriverManager.FIREFOX);
        testcase.initialize(selenesefile, "test", wdm.get(), "");

        Result result = click.doCommand(testcase);

        assertThat(result.getMessage(), is("failed command:Command#1: click(\"link=linktext\") result:Element link=linktext not found"));
    }
}
