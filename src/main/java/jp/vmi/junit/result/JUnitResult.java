package jp.vmi.junit.result;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import static jp.vmi.junit.result.ObjectFactory.*;

/**
 * Record and output test-suite & test-case results.
 * <p>
 * It expected that this is parsed by Jenkins.
 * </p>
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/SuiteResult.java">Jenkins SuiteResult class.</a>
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/CaseResult.java">Jenkins CaseResult class.</a>
 */
public final class JUnitResult {

    private static JAXBContext context = initContext();

    private static final Map<Object, TestResult<?>> map = new ConcurrentHashMap<Object, TestResult<?>>();

    private static String xmlResultDir = null;

    private static JAXBContext initContext() {
        try {
            return JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set directory for storing xml results.
     *
     * @param dir directory.
     */
    public static void setXmlResultDir(String dir) {
        xmlResultDir = dir;
    }

    /**
     * Start test-suite.
     *
     * @param testSuite test-suite instance.
     */
    public static void startTestSuite(ITestSuite testSuite) {
        map.put(testSuite, factory.createTestSuiteResult(testSuite));
    }

    /**
     * End test-suite.
     *
     * @param testSuite test-suite instatnce.
     */
    public static void endTestSuite(ITestSuite testSuite) {
        TestSuiteResult suiteResult = (TestSuiteResult) map.remove(testSuite);
        if (xmlResultDir == null || suiteResult.getTests() == 0)
            return;
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File file = new File(xmlResultDir, "TEST-" + suiteResult.getName() + ".xml");
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
        TestCaseResult caseResult = factory.createTestCaseResult(testCase);
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
        map.remove(testCase);
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
}
