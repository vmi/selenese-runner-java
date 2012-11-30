package jp.vmi.selenium.selenese.command;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.HtmlUnitDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for {@link BuiltInCommand}.
 */
public class BuiltInCommandTest {

    /**
     * Test of user friendly error message.
     *
     * @throws IOException exception.
     */
    @Test
    public void userFriendlyErrorMessage() throws IOException {
        Command click = new BuiltInCommand(1, "click", new String[] { "link=linktext" }, "click", false);

        File selenesefile = File.createTempFile("selenese", ".html");

        TestCase testcase = new TestCase();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(new HtmlUnitDriverFactory());
        Runner runner = new Runner();
        runner.setDriver(wdm.get());
        testcase.initialize(selenesefile, "test", runner, "");

        Result result = click.doCommand(testcase);

        assertThat(result.getMessage(), is("failed command:Command#1: click(\"link=linktext\") result:Element link=linktext not found"));
    }
}
