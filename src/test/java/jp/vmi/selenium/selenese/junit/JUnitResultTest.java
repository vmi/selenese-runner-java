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
        setResultDir(tmpDir.getRoot().getPath());
        TestSuite testSuite1 = Binder.newTestSuite(null, "test-suite1");
        TestSuite testSuite2 = Binder.newTestSuite(null, "test-suite2");
        startTestSuite(testSuite1);
        TestCase testCase = Binder.newTestCase(null, "test-case1", null, "");
        startTestCase(testCase);
        addFailure("failed1.");
        endTestCase();
        startTestSuite(testSuite2);
        testCase = Binder.newTestCase(null, "test-case2", null, "");
        startTestCase(testCase);
        addFailure("failed2.");
        endTestCase();
        endTestSuite();
        testCase = Binder.newTestCase(null, "test-case3", null, "");
        startTestCase(testCase);
        addFailure("failed3.");
        endTestCase();
        endTestSuite();
        for (File f : tmpDir.getRoot().listFiles()) {
            System.out.println("[" + f + "]");
            System.out.println(FileUtils.readFileToString(f, "UTF-8"));
        }
    }
}
