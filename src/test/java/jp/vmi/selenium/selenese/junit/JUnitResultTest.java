package jp.vmi.selenium.selenese.junit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import jp.vmi.selenium.selenese.inject.Binder;

import static jp.vmi.selenium.selenese.junit.JUnitResult.*;

public class JUnitResultTest {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void test() throws IOException {
        setOutputDir(tmpDir.getRoot().getPath());
        TestSuite testSuite = Binder.newTestSuite(new File("test-suite.html"));
        startTestSuite(testSuite);
        TestCase testCase = Binder.newTestCase(null, "test-case", null, "");
        startTestCase(testCase);
        addFailure("failed.");
        endTestCase();
        endTestSuite();
        for (File f : tmpDir.getRoot().listFiles()) {
            System.out.println("[" + f + "]");
            System.out.println(FileUtils.readFileToString(f, "UTF-8"));
        }
    }
}
