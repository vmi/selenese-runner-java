package jp.vmi.selenium.selenese.cmdproc;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import jp.vmi.selenium.selenese.TestBase;

import static org.junit.Assert.*;

/**
 * Test for {@link CustomCommandProcessor}
 * @author iwa
 *
 */
public class CustomCommandProcessorTest extends TestBase {

    /**
     * Test of getEval with "storedVars".
     */
    @Test
    @Ignore("htmlunit bug?")
    //TODO: ignore caused by fail.
    public void test() {
        HtmlUnitDriver driver = new HtmlUnitDriver(true);
        String baseUrl = ws.getUrl();
        CustomCommandProcessor proc = new CustomCommandProcessor(baseUrl, driver);
        proc.doCommand("open", new String[] { "/" });
        String script = "storedVars['logoutpresent'] ? storedVars['link_logout'] : storedVars['body']";
        proc.setVar("result-1", "link_logout");
        proc.setVar("result-2", "body");
        proc.setVar(true, "logoutpresent");
        String result;
        result = proc.doCommand("getEval", new String[] { script });
        assertEquals("result-1", result);
        proc.setVar(false, "logoutpresent");
        result = proc.doCommand("getEval", new String[] { script });
        assertEquals("result-2", result);
    }
}
