package jp.vmi.html.result;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import jp.vmi.selenium.selenese.Runner;
import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.command.CommandFactory;
import jp.vmi.selenium.selenese.inject.Binder;

/**
 * HTML result test.
 */
public class HtmlResultTest {

    @SuppressWarnings("javadoc")
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private String filename(File root, String name) {
        return new File(root, name + ".html").getPath();
    }

    /**
     * Generate HTML result. (old style)
     *
     * @throws Exception exception.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void generateHtmlResultOld() throws Exception {
        //File root = tmpDir.getRoot();
        File root = new File("/tmp");
        Runner runner = new Runner();
        CommandFactory cf = runner.getCommandFactory();
        runner.setDriver(new HtmlUnitDriver(true));
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
        //File root = tmpDir.getRoot();
        File root = new File("/tmp");
        Runner runner = new Runner();
        runner.setDriver(new HtmlUnitDriver(true));
        CommandFactory cf = runner.getCommandFactory();
        String s1name = "suite1";
        TestSuite s1 = Binder.newTestSuite(filename(root, s1name), s1name);
        String s2name = "suite2";
        TestSuite s2 = Binder.newTestSuite(filename(root, s2name), s2name);
        String c1name = "case1";
        TestCase c1 = Binder.newTestCase(filename(root, c1name), c1name, "http://localhost");
        c1.addCommand(cf, "echo", "c1");
        String c2name = "case2";
        TestCase c2 = Binder.newTestCase(filename(root, c2name), c2name, "http://localhost");
        c2.addCommand(cf, "echo", "c2");
        s2.addSelenese(c2);
        s1.addSelenese(c1);
        s1.addSelenese(s2);
        runner.setHtmlResultDir(root.getPath());
        runner.execute(s1);
        runner.finish();
    }
}
