package jp.vmi.html.result;

import java.io.File;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.WebDriver;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;
import jp.vmi.selenium.testutils.TestBase;
import jp.vmi.selenium.webdriver.DriverOptions;
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
     * Generate HTML result. (old style)
     *
     * @throws Exception exception.
     */
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void generateHtmlResultOld() throws Exception {
        setWebDriverFactory(WebDriverManager.HTMLUNIT, new DriverOptions());
        File root = getTmpRoot();
        Runner runner = new Runner();
        CommandFactory cf = runner.getCommandFactory();
        runner.setDriver(manager.get());
        String s1name = "suite1";
        TestSuite s1 = Binder.newTestSuite(filename(root, s1name), s1name, runner);
        String s2name = "suite2";
        TestSuite s2 = Binder.newTestSuite(filename(root, s2name), s2name, runner);
        String c1name = "case1";
        TestCase c1 = Binder.newTestCase(filename(root, c1name), c1name, runner, "http://localhost");
        c1.addCommand(cf.newCommand(0, "echo", "c1"));
        String c2name = "case2";
        TestCase c2 = Binder.newTestCase(filename(root, c2name), c2name, runner, "http://localhost");
        c2.addCommand(cf.newCommand(0, "echo", "c2"));
        s2.addSelenese(c2);
        s1.addSelenese(c1);
        s1.addSelenese(s2);
        runner.setHtmlResultDir(root.getPath());
        s1.execute(null, runner);
        runner.finish();
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
        @SuppressWarnings("deprecation")
        WebDriverManager wdm = WebDriverManager.getInstance();
        wdm.setWebDriverFactory(WebDriverManager.PHANTOMJS);
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
        TestCase c1 = Binder.newTestCase(filename(root, c1name), c1name, "http://localhost");
        c1.addCommand(cf, "store", "3", "index");
        c1.addCommand(cf, "while", "${index} > 0");
        c1.addCommand(cf, "open", "/form.html");
        c1.addCommand(cf, "storeEval", "${index} - 1", "index");
        c1.addCommand(cf, "endWhile");
        String c2name = "case2";
        TestCase c2 = Binder.newTestCase(filename(root, c2name), c2name, "http://localhost");
        c2.addCommand(cf, "open", "/form2.html");
        s2.addSelenese(c2);
        s1.addSelenese(c1);
        s1.addSelenese(s2);
        runner.execute(s1);
        runner.finish();
    }
}
