package jp.vmi.selenium.selenese.command;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestBase;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.result.Result;
import jp.vmi.selenium.webdriver.HtmlUnitDriverFactory;
import jp.vmi.selenium.webdriver.WebDriverManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Test for {@link BuiltInCommand}.
 */
public class BuiltInCommandTest extends TestBase {

    /**
    * Test of user friendly error message.
    *
    * @throws IOException exception.
    */
    @Test
    @Ignore("test fail on buildhive....")
    public void userFriendlyErrorMessage() throws IOException {
        Command click = new BuiltInCommand(1, "click", new String[] { "link=linktext" }, "click", false);
        Command open = new Open(1, "open", new String[] { "/index.html" }, "open", false);

        File selenesefile = File.createTempFile("selenese", ".html");

        TestCase testcase = new TestCase();
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(new HtmlUnitDriverFactory());
        Runner runner = new Runner();
        runner.setDriver(wdm.get());
        testcase.initialize(selenesefile, "test", runner, ws.getUrl());

        assertTrue(open.doCommand(testcase).isSuccess());
        Result result = click.doCommand(testcase);

        assertThat(result.getMessage(), is("Failure: Element link=linktext not found"));
    }
}
