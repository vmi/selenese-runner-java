package jp.vmi.selenium.selenese.junit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import junit.framework.AssertionFailedError;

public final class JUnitResult {

    private static final ThreadLocal<Formatter> currentFormatter = new ThreadLocal<Formatter>();

    private static String outputDir = ".";

    private static class Formatter {

        private final XMLJUnitResultFormatter formatter;
        private final OutputStream out;
        private final JUnitTest jUnitTest;
        private TestCase testCase;

        private Formatter(String name) {
            formatter = new XMLJUnitResultFormatter();
            try {
                out = new FileOutputStream(new File(outputDir, "TEST-" + name + ".xml"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            formatter.setOutput(out);
            jUnitTest = new JUnitTest(name);
            formatter.startTestSuite(jUnitTest);
        }

        private void endTestSuite() {
            formatter.endTestSuite(jUnitTest);
            IOUtils.closeQuietly(out);
        }

        private void startTestCase(TestCase testCase) {
            this.testCase = testCase;
            formatter.startTest(testCase);
        }

        private void endTestCase() {
            formatter.endTest(testCase);
        }

        private void addError(Throwable t) {
            formatter.addError(testCase, t);
        }

        public void addFailure(Throwable t) {
            formatter.addFailure(testCase, t);
        }

        public void addFailure(String message) {
            formatter.addFailure(testCase, new AssertionFailedError(message));
        }
    }

    public static void setOutputDir(String dir) {
        outputDir = dir;
    }

    public static void startTestSuite(TestSuite testSuite) {
        Formatter result = new Formatter(testSuite.getName());
        currentFormatter.set(result);
    }

    public static void endTestSuite() {
        currentFormatter.get().endTestSuite();
        currentFormatter.remove();
    }

    public static void startTestCase(TestCase testCase) {
        currentFormatter.get().startTestCase(testCase);
    }

    public static void endTestCase() {
        currentFormatter.get().endTestCase();
    }

    public static void addError(Throwable t) {
        currentFormatter.get().addError(t);
    }

    public static void addFailure(Throwable t) {
        currentFormatter.get().addFailure(t);
    }

    public static void addFailure(String message) {
        currentFormatter.get().addFailure(message);
    }
}
