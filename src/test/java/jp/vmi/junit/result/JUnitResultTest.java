package jp.vmi.junit.result;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static jp.vmi.junit.result.JUnitResult.*;

/**
 * Test of {@link JUnitResult}.
 */
public class JUnitResultTest {

    /** temporary folder. */
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    /**
     * initialize for test.
     *
     * @throws IOException exception.
     */
    @Before
    public void init() throws IOException {
        setResultDir(tmp.getRoot().getPath());
    }

    /**
     * finalize for test.
     */
    @After
    public void finish() {
        setResultDir(null);
    }

    /**
     * Test for methods of {@link JUnitResult}.
     *
     * @throws Exception exception.
     */
    @Test
    @SuppressWarnings("unused")
    public void testAll() throws Exception {
        Date now = new Date();
        final ITestSuite testSuite = new ITestSuite() {
            @Override
            public String getName() {
                return "test-suite";
            }

            @Override
            public boolean isError() {
                return false;
            }
        };
        ITestCase[] testCases = new ITestCase[4];
        for (int i = 0; i < testCases.length; i++) {
            final int num = i;
            testCases[i] = new ITestCase() {

                @Override
                public String getName() {
                    return "test-case" + num;
                }

                @Override
                public boolean isError() {
                    return false;
                }
            };
        }
        startTestSuite(testSuite);
        addProperty(testSuite, "prop-name1", "prop-value1");
        addProperty(testSuite, "prop-name2", "prop-value2");
        addProperty(testSuite, "prop-name3", "prop-value3");
        ITestCase tc;
        tc = testCases[0];
        startTestCase(testSuite, tc);
        setSuccess(tc);
        logInfo(tc, "systemOut00");
        logError(tc, "systemErr00");
        logInfo(tc, "systemOut01");
        logError(tc, "systemErr01");
        logInfo(tc, "systemOut02");
        logError(tc, "systemErr02");
        Thread.sleep(100);
        endTestCase(tc);
        tc = testCases[1];
        startTestCase(testSuite, tc);
        setError(tc, "detail1", "trace1");
        addSystemOut(tc, "systemOut1");
        addSystemErr(tc, "systemErr1");
        Thread.sleep(50);
        endTestCase(tc);
        tc = testCases[2];
        startTestCase(testSuite, tc);
        setFailure(tc, "detail2", "trace2");
        addSystemOut(tc, "systemOut2");
        addSystemErr(tc, "systemErr2");
        Thread.sleep(10);
        endTestCase(tc);
        tc = testCases[3];
        startTestCase(testSuite, tc);
        endTestCase(tc);
        endTestSuite(testSuite);
        for (File file : tmp.getRoot().listFiles()) {
            try {
                System.out.printf("[%s]%n", file);
                String body = FileUtils.readFileToString(file, "UTF-8");
                System.out.println(body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File resultFile = new File(tmp.getRoot(), "TEST-" + testSuite.getName() + ".xml");
        //        List<SuiteResult> suiteResults = JenkinsSuiteResult.parse(resultFile);
        //        // test-suite test.
        //        SuiteResult suiteResult = suiteResults.get(0);
        //        assertEquals("test-suite", suiteResult.getName());
        //        float d = suiteResult.getDuration();
        //        assertTrue(0 < d && d < 1.0);
        //        String timestamp = suiteResult.getTimestamp();
        //        Date suiteTimestamp = DateUtils.parseIso8601DateTime(timestamp);
        //        long delta = suiteTimestamp.getTime() - now.getTime();
        //        assertTrue(delta <= 1000);
        //        List<CaseResult> caseResults = suiteResult.getCases();
        //        CaseResult caseResult;
        //        // test-case 0 test.
        //        caseResult = caseResults.get(0);
        //        assertNotSame(Result.UNSTABLE, caseResult.getBuildResult());
        //        assertEquals("test-suite", caseResult.getClassName());
        //        assertEquals("test-case0", caseResult.getDisplayName());
        //        assertTrue(caseResult.getDuration() < 0.2);
        //        assertNull(caseResult.getErrorDetails());
        //        assertNull(caseResult.getErrorStackTrace());
        //        assertEquals(1, caseResult.getPassCount());
        //        assertEquals(0, caseResult.getFailCount());
        //        assertEquals(0, caseResult.getSkipCount());
        //        assertTrue(caseResult.getStdout().matches("(?s).*systemOut00.*\n.*systemOut01.*\n.*systemOut02.*"));
        //        assertTrue(caseResult.getStderr().matches("(?s).*systemErr00.*\n.*systemErr01.*\n.*systemErr02.*"));
        //        // test-case 1 test.
        //        caseResult = caseResults.get(1);
        //        assertEquals(Result.UNSTABLE, caseResult.getBuildResult());
        //        assertEquals("detail1", caseResult.getErrorDetails());
        //        assertEquals("trace1", caseResult.getErrorStackTrace());
        //        assertEquals(0, caseResult.getPassCount());
        //        assertEquals(1, caseResult.getFailCount());
        //        assertEquals(0, caseResult.getSkipCount());
        //        // test-case 2 test.
        //        caseResult = caseResults.get(2);
        //        assertEquals(Result.UNSTABLE, caseResult.getBuildResult());
        //        assertEquals("detail2", caseResult.getErrorDetails());
        //        assertEquals("trace2", caseResult.getErrorStackTrace());
        //        assertEquals(0, caseResult.getPassCount());
        //        assertEquals(1, caseResult.getFailCount());
        //        assertEquals(0, caseResult.getSkipCount());
        //        // test-case 3 test.
        //        caseResult = caseResults.get(3);
        //        assertNotSame(Result.UNSTABLE, caseResult.getBuildResult());
        //        assertEquals(0, caseResult.getPassCount());
        //        assertEquals(0, caseResult.getFailCount());
        //        assertEquals(1, caseResult.getSkipCount());
    }
}
