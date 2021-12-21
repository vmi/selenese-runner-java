package jp.vmi.html.result;

import java.io.File;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.SourceType;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.WebDriverManager;

/**
 * HTML result test.
 */
public class HtmlResultTest extends TestBase {

    @SuppressWarnings("javadoc")
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private File getTmpRoot() {
        if (System.getenv("USE_IDE") != null)
            return new File("/tmp");
        else
            return tmpDir.getRoot();
    }

    private String filename(File root, String name) {
        return new File(root, name + ".html").getPath();
    }

    /**
     * Generate HTML result.
     *
     * @throws Exception exception.
     */
    @Test
    public void generateHtmlResult() throws Exception {
        File root = getTmpRoot();
        new File(root, "html").mkdir();
        new File(root, "img").mkdir();
        Runner runner = new Runner();
        WebDriverManager wdm = WebDriverManager.newInstance();
        wdm.setWebDriverFactory(WebDriverManager.HTMLUNIT);
        WebDriver driver = null;
        try {
            driver = wdm.get();
        } catch (RuntimeException e) {
            Assume.assumeNoException(e);
        }
        runner.setDriver(driver);
        runner.setHtmlResultDir(new File(root, "html").getPath());
        runner.setScreenshotAllDir(new File(root, "img").getPath());
        CommandFactory cf = runner.getCommandFactory();
        String s1name = "suite1";
        TestSuite s1 = Binder.newTestSuite(filename(root, s1name), s1name);
        String s2name = "suite2";
        TestSuite s2 = Binder.newTestSuite(filename(root, s2name), s2name);
        String c1name = "case1";
        TestCase c1 = Binder.newTestCase(SourceType.SELENESE, filename(root, c1name), c1name, "http://localhost");
        c1.addCommand(cf, "store", "3", "index");
        c1.addCommand(cf, "while", "${index} > 0");
        c1.addCommand(cf, "open", "/form.html");
        c1.addCommand(cf, "storeEval", "${index} - 1", "index");
        c1.addCommand(cf, "endWhile");
        String c2name = "case2";
        TestCase c2 = Binder.newTestCase(SourceType.SELENESE, filename(root, c2name), c2name, "http://localhost");
        c2.addCommand(cf, "open", "/form2.html");
        s2.addSelenese(c2);
        s1.addSelenese(c1);
        s1.addSelenese(s2);
        s1.addSelenese(c2);
        runner.execute(s1);
        runner.finish();
    }
}
