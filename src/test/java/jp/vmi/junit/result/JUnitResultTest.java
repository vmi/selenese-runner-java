package jp.vmi.junit.result;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jp.vmi.selenium.selenese.ITreedFileGenerator;
import jp.vmi.selenium.selenese.utils.DateTimeUtils;
import jp.vmi.selenium.selenese.utils.LogRecorder;
import jp.vmi.selenium.selenese.utils.StopWatch;

import static java.lang.Boolean.*;
import static javax.xml.xpath.XPathConstants.*;
import static jp.vmi.junit.result.JUnitResultTest.RegexMatcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Test of {@link JUnitResult}.
 */
@SuppressWarnings("javadoc")
public class JUnitResultTest {

    // See http://piotrga.wordpress.com/2009/03/27/hamcrest-regex-matcher/
    public static class RegexMatcher extends BaseMatcher<String> {

        private final String regex;

        public RegexMatcher(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean matches(Object o) {
            return ((String) o).matches(regex);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("matches regex=");
        }

        public static RegexMatcher matches(String regex) {
            return new RegexMatcher(regex);
        }
    }

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    public JUnitResult jur;

    public XPath xpath;

    /**
     * initialize for test.
     *
     * @throws IOException exception.
     */
    @Before
    public void init() throws IOException {
        jur = new JUnitResult();
        jur.setDir(tmp.getRoot().getPath());
        xpath = XPathFactory.newInstance().newXPath();
    }

    /**
     * finalize for test.
     */
    @After
    public void finish() {
        jur = null;
    }

    /**
     * Test for methods of {@link JUnitResult}.
     *
     * @throws Exception exception.
     */
    @Test
    public void testJUnitResult() throws Exception {
        Instant start = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        final ITestSuite testSuite = new ITestSuite() {
            private int index = 0;
            private final StopWatch stopWatch = new StopWatch();

            @Override
            public ITreedFileGenerator getParent() {
                return null;
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public void setIndex(int index) {
                this.index = index;
            }

            @Override
            public String getBaseName() {
                return getName();
            }

            @Override
            public String getName() {
                return "test-suite";
            }

            @Override
            public boolean isError() {
                return false;
            }

            @Override
            public StopWatch getStopWatch() {
                return stopWatch;
            }
        };
        ITestCase[] testCases = new ITestCase[4];
        for (int i = 0; i < testCases.length; i++) {
            final int num = i;
            testCases[i] = new ITestCase() {
                private final StopWatch stopWatch = new StopWatch();
                private final LogRecorder logRecorder = new LogRecorder(System.out);

                @Override
                public String getBaseName() {
                    return getName();
                }

                @Override
                public String getName() {
                    return "test-case" + num;
                }

                @Override
                public boolean isError() {
                    return false;
                }

                @Override
                public StopWatch getStopWatch() {
                    return stopWatch;
                }

                @Override
                public void setLogRecorder(LogRecorder logRecorder) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public LogRecorder getLogRecorder() {
                    return logRecorder;
                }
            };
        }
        jur.startTestSuite(testSuite);
        jur.addProperty(testSuite, "prop-name1", "prop-value1");
        jur.addProperty(testSuite, "prop-name2", "prop-value2");
        jur.addProperty(testSuite, "prop-name3", "prop-value3");
        ITestCase tc;
        StopWatch sw;
        LogRecorder clr;
        tc = testCases[0];
        sw = tc.getStopWatch();
        clr = tc.getLogRecorder();
        jur.startTestCase(testSuite, tc);
        testSuite.getStopWatch().start();
        sw.start();
        jur.setSuccess(tc);
        clr.info("systemOut00");
        clr.error("systemErr00");
        clr.info("systemOut01");
        clr.error("systemErr01");
        clr.info("systemOut02");
        clr.error("systemErr02");
        Thread.sleep(100);
        sw.end();
        jur.endTestCase(tc);
        tc = testCases[1];
        sw = tc.getStopWatch();
        clr = tc.getLogRecorder();
        jur.startTestCase(testSuite, tc);
        sw.start();
        jur.setError(tc, "detail1", "trace1");
        clr.info("systemOut1");
        clr.error("systemErr1");
        Thread.sleep(50);
        sw.end();
        jur.endTestCase(tc);
        tc = testCases[2];
        sw = tc.getStopWatch();
        clr = tc.getLogRecorder();
        jur.startTestCase(testSuite, tc);
        sw.start();
        jur.setFailure(tc, "detail2", "trace2");
        clr.info("systemOut2");
        clr.error("systemErr2");
        Thread.sleep(10);
        sw.end();
        jur.endTestCase(tc);
        tc = testCases[3];
        sw = tc.getStopWatch();
        jur.startTestCase(testSuite, tc);
        sw.start();
        sw.end();
        jur.endTestCase(tc);
        testSuite.getStopWatch().end();
        jur.endTestSuite(testSuite);
        jur.generateFailsafeSummary();
        dumpFiles();

        // test-suite test.
        File resultFile = new File(tmp.getRoot(), "TEST-" + testSuite.getName() + ".xml");
        Element suiteResult = (Element) xpath.evaluate("/testsuite", new InputSource(resultFile.getPath()), NODE);
        assertThat(suiteResult.getAttribute("name"), is("test-suite"));
        assertThat("test-suite:time", Double.parseDouble(suiteResult.getAttribute("time")), lessThan(1.0));
        String timestamp = suiteResult.getAttribute("timestamp");
        Instant suiteTimestamp = Instant.from(DateTimeUtils.parseIso8601(timestamp));
        assertThat(suiteTimestamp, greaterThanOrEqualTo(start));

        // test-case test.
        NodeList caseResults = getChild(suiteResult, "testcase", NODESET);
        assertThat("test-case:count", caseResults.getLength(), is(4));

        Element caseResult;
        Element error;
        Element failure;
        Boolean skipped;
        String sysout;
        String syserr;

        // test-case 0 test.
        caseResult = (Element) caseResults.item(0);
        assertThat("test-case[0]:name", caseResult.getAttribute("name"), is("test-case0"));
        assertThat("test-case[0]:time", Double.parseDouble(caseResult.getAttribute("time")), lessThan(1.0));

        error = getChild(caseResult, "error", NODE);
        failure = getChild(caseResult, "failure", NODE);
        skipped = getChild(caseResult, "skipped", BOOLEAN);
        assertThat("test-case[0]:error", error, is(nullValue()));
        assertThat("test-case[0]:failure", failure, is(nullValue()));
        assertThat("test-case[0]:skipped", skipped, is(FALSE));

        sysout = getChild(caseResult, "system-out", STRING);
        syserr = getChild(caseResult, "system-err", STRING);
        assertThat("test-case[0]:system-out", sysout, matches("(?s).*systemOut00.*\n.*systemOut01.*\n.*systemOut02.*"));
        assertThat("test-case[0]:system-err", syserr, matches("(?s).*systemErr00.*\n.*systemErr01.*\n.*systemErr02.*"));

        // test-case 1 test.
        caseResult = (Element) caseResults.item(1);
        assertThat("test-case[1]:name", caseResult.getAttribute("name"), is("test-case1"));

        error = getChild(caseResult, "error", NODE);
        failure = getChild(caseResult, "failure", NODE);
        skipped = getChild(caseResult, "skipped", BOOLEAN);
        assertThat("test-case[1]:error", error.getTextContent(), is("trace1"));
        assertThat("test-case[1]:error@message", error.getAttribute("message"), is("detail1"));
        assertThat("test-case[1]:failure", failure, is(nullValue()));
        assertThat("test-case[1]:skipped", skipped, is(FALSE));

        // test-case 2 test.
        caseResult = (Element) caseResults.item(2);
        assertThat("test-case[2]:name", caseResult.getAttribute("name"), is("test-case2"));

        error = getChild(caseResult, "error", NODE);
        failure = getChild(caseResult, "failure", NODE);
        skipped = getChild(caseResult, "skipped", BOOLEAN);
        assertThat("test-case[2]:error", error, is(nullValue()));
        assertThat("test-case[2]:failure", failure.getTextContent(), is("trace2"));
        assertThat("test-case[2]:failure@message", failure.getAttribute("message"), is("detail2"));
        assertThat("test-case[2]:skipped", skipped, is(FALSE));

        // test-case 3 test.
        caseResult = (Element) caseResults.item(3);
        assertThat("test-case[3]:name", caseResult.getAttribute("name"), is("test-case3"));

        error = getChild(caseResult, "error", NODE);
        failure = getChild(caseResult, "failure", NODE);
        skipped = getChild(caseResult, "skipped", BOOLEAN);
        assertThat("test-case[3]:error", error, is(nullValue()));
        assertThat("test-case[3]:failure", failure, is(nullValue()));
        assertThat("test-case[2]:skipped", skipped, is(TRUE));

        // failsafe summary.
        File summaryFile = new File(tmp.getRoot(), JUnitResult.FAILSAFE_SUMMARY_FILENAME);
        Element summary = (Element) xpath.evaluate("/failsafe-summary", new InputSource(summaryFile.getPath()), NODE);
        assertThat("failsafe-summary", summary, is(not(nullValue())));
        assertThat("failsafe-summary@result", summary.getAttribute("result"), is("255"));
        assertThat("failsafe-summary@timeout", summary.getAttribute("timeout"), is("false"));
        assertThat("failsafe-summary/completed", (String) getChild(summary, "completed", STRING), is("3"));
        assertThat("failsafe-summary/errors", (String) getChild(summary, "errors", STRING), is("1"));
        assertThat("failsafe-summary/failures", (String) getChild(summary, "failures", STRING), is("1"));
        assertThat("failsafe-summary/skipped", (String) getChild(summary, "skipped", STRING), is("1"));
        assertThat("failsafe-summary/failureMessage", (String) getChild(summary, "failureMessage", STRING), isEmptyString());
    }

    @Test
    public void testFailsafeSummaryOnSuccess() throws Exception {
        jur.generateFailsafeSummary();
        dumpFiles();

        File summaryFile = new File(tmp.getRoot(), JUnitResult.FAILSAFE_SUMMARY_FILENAME);
        Element summary = (Element) xpath.evaluate("/failsafe-summary", new InputSource(summaryFile.getPath()), NODE);
        assertThat("failsafe-summary", summary, is(not(nullValue())));
        assertThat("failsafe-summary@result", summary.getAttribute("result"), isEmptyOrNullString());
        assertThat("failsafe-summary@timeout", summary.getAttribute("timeout"), is("false"));
        assertThat("failsafe-summary/completed", (String) getChild(summary, "completed", STRING), is("0"));
        assertThat("failsafe-summary/errors", (String) getChild(summary, "errors", STRING), is("0"));
        assertThat("failsafe-summary/failures", (String) getChild(summary, "failures", STRING), is("0"));
        assertThat("failsafe-summary/skipped", (String) getChild(summary, "skipped", STRING), is("0"));
        assertThat("failsafe-summary/failureMessage", (String) getChild(summary, "failureMessage", STRING), isEmptyString());
    }

    private void dumpFiles() {
        for (File file : tmp.getRoot().listFiles()) {
            try {
                System.out.printf("[%s]%n", file);
                String body = FileUtils.readFileToString(file, "UTF-8");
                System.out.println(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getChild(Node node, String name, QName type) {
        try {
            return (T) xpath.evaluate("./" + name, node, type);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }
}
