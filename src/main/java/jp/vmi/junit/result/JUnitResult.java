package jp.vmi.junit.result;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang.time.FastDateFormat;
import org.jboss.netty.util.internal.ConcurrentIdentityHashMap;

/**
 * Record and output test-suite & test-case results.
 * <p>
 * It expected that this is parsed by Jenkins.
 * </p>
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/SuiteResult.java">Jenkins SuiteResult class.</a>
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/CaseResult.java">Jenkins CaseResult class.</a>
 */
public final class JUnitResult {

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("[yyyy-MM-dd HH:mm:ss.SSS] ");

    private static JAXBContext context;

    private static final Map<Object, TestResult> map = new ConcurrentIdentityHashMap<Object, TestResult>();

    private static String resultDir = null;

    static {
        try {
            context = JAXBContext.newInstance(TestSuiteResult.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set directory for storing results.
     *
     * @param dir directory.
     */
    public static void setResultDir(String dir) {
        resultDir = dir;
    }

    /**
     * Start test-suite.
     *
     * @param testSuite test-suite instance.
     */
    public static void startTestSuite(ITestSuite testSuite) {
        map.put(testSuite, new TestSuiteResult(testSuite.getName()));
    }

    /**
     * End test-suite.
     *
     * @param testSuite test-suite instatnce.
     */
    public static void endTestSuite(ITestSuite testSuite) {
        TestSuiteResult suiteResult = (TestSuiteResult) map.remove(testSuite);
        suiteResult.endTestSuite();
        if (resultDir == null)
            return;
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File file = new File(resultDir, "TEST-" + suiteResult.getName() + ".xml");
            marshaller.marshal(suiteResult, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add property in test-suite.
     *
     * @param testSuite test-suite instatnce.
     * @param name property name.
     * @param value property value.
     */
    public static void addProperty(ITestSuite testSuite, String name, String value) {
        TestSuiteResult suiteResult = (TestSuiteResult) map.get(testSuite);
        suiteResult.addProperty(name, value);
    }

    /**
     * Start test-case.
     * @param testSuite test-suite instance.
     * @param testCase test-case instance.
     */
    public static void startTestCase(ITestSuite testSuite, ITestCase testCase) {
        TestCaseResult caseResult = new TestCaseResult(testCase.getName());
        map.put(testCase, caseResult);
        if (testSuite != null) {
            TestSuiteResult suiteResult = (TestSuiteResult) map.get(testSuite);
            suiteResult.addTestCaseResult(caseResult);
        }
    }

    /**
     * End test-case.
     *
     * @param testCase test-case instance.
     */
    public static void endTestCase(ITestCase testCase) {
        TestCaseResult caseResult = (TestCaseResult) map.remove(testCase);
        caseResult.endTestCase();
    }

    /**
     * Set success.
     *
     * @param testCase test-case instance.
     */
    public static void setSuccess(ITestCase testCase) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.setSuccess();
    }

    /**
     * Set error in test-case.
     *
     * @param testCase test-case instance.
     * @param message error message.
     * @param trace error trace.
     */
    public static void setError(ITestCase testCase, String message, String trace) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.setError(message, trace);
    }

    /**
     * Set failure in test-case.
     *
     * @param testCase test-case instance.
     * @param message error message.
     * @param trace error trace.
     */
    public static void setFailure(ITestCase testCase, String message, String trace) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.setFailure(message, trace);
    }

    /**
     * Add System.out'ed message string.
     *
     * @param testCase test-case instance.
     * @param message system-out message.
     */
    public static void addSystemOut(ITestCase testCase, String message) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.addSystemOut(message);
    }

    /**
     * Add System.err'ed message string.
     *
     * @param testCase test-case instance.
     * @param message system-err message.
     */
    public static void addSystemErr(ITestCase testCase, String message) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.addSystemErr(message);
    }

    private static String logFormat(String level, String... messages) {
        StringBuilder logMsg = new StringBuilder(DATE_TIME_FORMAT.format(System.currentTimeMillis()));
        logMsg.append(level);
        for (String message : messages)
            logMsg.append(" ").append(message);
        return logMsg.toString();
    }

    /**
     * Add System.out'ed message string.
     *
     * @param testCase test-case instance.
     * @param messages info log messages.
     */
    public static void logInfo(ITestCase testCase, String... messages) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.addSystemOut(logFormat("[INFO]", messages));
    }

    /**
     * Add System.err'ed message string.
     *
     * @param testCase test-case instance.
     * @param messages error log messages.
     */
    public static void logError(ITestCase testCase, String... messages) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.addSystemErr(logFormat("[ERROR]", messages));
    }
}
