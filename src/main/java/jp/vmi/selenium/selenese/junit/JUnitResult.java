package jp.vmi.selenium.selenese.junit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.TestCase;
import jp.vmi.selenium.selenese.TestSuite;
import junit.framework.AssertionFailedError;

public final class JUnitResult {

    private static final Logger log = LoggerFactory.getLogger(JUnitResult.class);

    private static class Formatter {

        private final XMLJUnitResultFormatter formatter;
        private final File file;
        private final OutputStream out;
        private final JUnitTest jUnitTest;
        private TestCase testCase = null;

        // for NULL_FORMATTER
        public Formatter() {
            formatter = null;
            file = null;
            out = null;
            jUnitTest = null;
        }

        public Formatter(String name) {
            formatter = new XMLJUnitResultFormatter();
            try {
                file = new File(resultDir, "TEST-" + name + ".xml");
                out = new FileOutputStream(file);
                log.info("Open XML Result File: {}", file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            formatter.setOutput(out);
            jUnitTest = new JUnitTest(name);
            formatter.startTestSuite(jUnitTest);
        }

        public void endTestSuite() {
            formatter.endTestSuite(jUnitTest);
            IOUtils.closeQuietly(out);
            log.info("Close XML Result File: {}", file);
        }

        public void startTestCase(TestCase testCase) {
            this.testCase = testCase;
            formatter.startTest(testCase);
        }

        public void endTestCase() {
            formatter.endTest(testCase);
        }

        public void addError(Throwable t) {
            formatter.addError(testCase, t);
        }

        public void addFailure(Throwable t) {
            formatter.addFailure(testCase, t);
        }

        public void addFailure(String message) {
            formatter.addFailure(testCase, new AssertionFailedError(message));
        }

        public void addSystemOut(String formattedMessage) {
            formatter.setSystemOutput(formattedMessage);
        }

        public void addSystemErr(String formattedMessage) {
            formatter.setSystemError(formattedMessage);
        }
    }

    private static final Formatter NULL_FORMATTER = new Formatter() {

        @Override
        public void endTestSuite() {
        }

        @Override
        public void startTestCase(TestCase testCase) {
        }

        @Override
        public void endTestCase() {
        }

        @Override
        public void addError(Throwable t) {
        }

        @Override
        public void addFailure(Throwable t) {
        }

        @Override
        public void addFailure(String message) {
        }
    };

    private static final ThreadLocal<Deque<TestSuite>> currentTestSuite = new ThreadLocal<Deque<TestSuite>>();
    private static final Map<TestSuite, Formatter> formatterMap = new ConcurrentHashMap<TestSuite, JUnitResult.Formatter>();
    private static String resultDir = null;

    private static Formatter getFormatter() {
        Deque<TestSuite> deque = currentTestSuite.get();
        if (deque == null)
            return NULL_FORMATTER;
        Formatter formatter = formatterMap.get(deque.peekFirst());
        if (formatter == null)
            return NULL_FORMATTER;
        return formatter;
    }

    public static void setResultDir(String dir) {
        resultDir = dir;
    }

    public static void startTestSuite(TestSuite testSuite) {
        Deque<TestSuite> deque = currentTestSuite.get();
        if (deque == null) {
            deque = new ArrayDeque<TestSuite>();
            currentTestSuite.set(deque);
        }
        deque.addFirst(testSuite);
        if (resultDir == null)
            return;
        Formatter formatter = new Formatter(testSuite.getName());
        formatterMap.put(testSuite, formatter);
    }

    public static void endTestSuite() {
        Formatter formatter = formatterMap.remove(currentTestSuite.get().pollFirst());
        if (formatter == null)
            return;
        formatter.endTestSuite();
    }

    public static void startTestCase(TestCase testCase) {
        getFormatter().startTestCase(testCase);
    }

    public static void endTestCase() {
        getFormatter().endTestCase();
    }

    public static void addError(Throwable t) {
        getFormatter().addError(t);
    }

    public static void addFailure(Throwable t) {
        getFormatter().addFailure(t);
    }

    public static void addFailure(String message) {
        getFormatter().addFailure(message);
    }

    public static void addSystemOut(String formattedMessage) {
        getFormatter().addSystemOut(formattedMessage);
    }

    public static void addSystemErr(String formattedMessage) {
        getFormatter().addSystemErr(formattedMessage);
    }
}
