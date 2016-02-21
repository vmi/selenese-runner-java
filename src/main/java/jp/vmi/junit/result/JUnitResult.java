package jp.vmi.junit.result;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.vmi.selenium.selenese.utils.CommandLineUtils;

import static jp.vmi.junit.result.ObjectFactory.*;

/**
 * Record and output test-suite &amp; test-case results.
 * <p>
 * It expected that this is parsed by Jenkins.
 * </p>
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/SuiteResult.java">Jenkins SuiteResult class.</a>
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/core/src/main/java/hudson/tasks/junit/CaseResult.java">Jenkins CaseResult class.</a>
 */
public final class JUnitResult {

    private static final Logger log = LoggerFactory.getLogger(JUnitResult.class);

    /** filename of failsafe-summary. */
    public static final String FAILSAFE_SUMMARY_FILENAME = "failsafe-summary.xml";

    private static final String[] PROP_NAMES = {
        "java.vm.name",
        "java.version",
        "os.name",
        "os.version",
        "os.arch",
        "user.language",
        "user.country",
        "user.timezone",
    };

    private String[] commandLineArgs = null;

    private String xmlResultDir = null;

    private final JAXBContext context = initContext();

    private final Map<Object, TestResult<?>> map = new ConcurrentHashMap<>();

    private final FailsafeSummary failsafeSummary = factory.createFailsafeSummary();

    private JAXBContext initContext() {
        try {
            return JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set command line arguments.
     *
     * @param args command line arguments.
     */
    public void setCommandLineArgs(String[] args) {
        commandLineArgs = args;
    }

    /**
     * Set XML result directory.
     *
     * @param dir XML result directory.
     */
    public void setDir(String dir) {
        this.xmlResultDir = dir;
    }

    protected void mkdirs() {
        File dir = new File(xmlResultDir);
        if (!dir.exists()) {
            dir.mkdirs();
            log.info("Make the directory for XML result: {}", dir);
        }
    }

    /**
     * Start test-suite.
     *
     * @param testSuite test-suite instance.
     */
    public void startTestSuite(ITestSuite testSuite) {
        TestSuiteResult suiteResult = factory.createTestSuiteResult(testSuite);
        for (String propName : PROP_NAMES)
            suiteResult.addProperty(propName, System.getProperty(propName, "<UNKNOWN>"));
        if (commandLineArgs != null)
            suiteResult.addProperty("seleneseRunner.args", CommandLineUtils.espaceCommandLineArgs(commandLineArgs));
        map.put(testSuite, suiteResult);
    }

    /**
     * End test-suite.
     *
     * @param testSuite test-suite instatnce.
     */
    public void endTestSuite(ITestSuite testSuite) {
        TestSuiteResult suiteResult = (TestSuiteResult) map.remove(testSuite);
        if (xmlResultDir == null || suiteResult.getTests() == 0)
            return;
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mkdirs();
            File file = new File(xmlResultDir, "TEST-" + suiteResult.getBaseName() + ".xml");
            marshaller.marshal(suiteResult, file);
            log.info("Generated JUnit result: {}", file);
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
    public void addProperty(ITestSuite testSuite, String name, String value) {
        TestSuiteResult suiteResult = (TestSuiteResult) map.get(testSuite);
        suiteResult.addProperty(name, value);
    }

    /**
     * Start test-case.
     * @param testSuite test-suite instance.
     * @param testCase test-case instance.
     */
    public void startTestCase(ITestSuite testSuite, ITestCase testCase) {
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
    public void endTestCase(ITestCase testCase) {
        TestCaseResult caseResult = (TestCaseResult) map.remove(testCase);
        failsafeSummary.skipped += caseResult.getSkipped();
    }

    /**
     * Set success.
     *
     * @param testCase test-case instance.
     */
    public void setSuccess(ITestCase testCase) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.setSuccess();
        failsafeSummary.completed++;
    }

    /**
     * Set error in test-case.
     *
     * @param testCase test-case instance.
     * @param message error message.
     * @param trace error trace.
     */
    public void setError(ITestCase testCase, String message, String trace) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.setError(message, trace);
        failsafeSummary.completed++;
        failsafeSummary.errors++;
    }

    /**
     * Set failure in test-case.
     *
     * @param testCase test-case instance.
     * @param message error message.
     * @param trace error trace.
     */
    public void setFailure(ITestCase testCase, String message, String trace) {
        TestCaseResult caseResult = (TestCaseResult) map.get(testCase);
        caseResult.setFailure(message, trace);
        failsafeSummary.completed++;
        failsafeSummary.failures++;
    }

    /**
     * Generate "failsafe-summary.xml" into XML result directory.
     */
    public void generateFailsafeSummary() {
        if (xmlResultDir == null)
            return;
        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mkdirs();
            File file = new File(xmlResultDir, FAILSAFE_SUMMARY_FILENAME);
            marshaller.marshal(failsafeSummary, file);
            log.info("Generated failsafe summary: {}", file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
