package jp.vmi.selenium.selenese;

import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Test for issue #75.
 */
@SuppressWarnings("javadoc")
public class Issue75Test {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void test() {
        Runner runner = new Runner();

        runner.setDriver(new HtmlUnitDriver(true));
        String tmpDir = tmpFolder.getRoot().getPath();
        runner.setJUnitResultDir(tmpDir);
        runner.setHtmlResultDir(tmpDir);
        String filename = getClass().getSimpleName() + ".html";
        InputStream is = Issue75Test.class.getResourceAsStream(filename);
        runner.run(filename, is);
    }
}
