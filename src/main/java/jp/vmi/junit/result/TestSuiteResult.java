package jp.vmi.junit.result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static jp.vmi.junit.result.ObjectFactory.*;

/**
 * Result of test-suite.
 */
@XmlRootElement(name = "testsuite")
@XmlType(propOrder = {
    "properties",
    "error",
    "testCaseResults"
})
public class TestSuiteResult extends TestResult<ITestSuite> {

    @XmlAttribute
    private Integer id;

    @XmlAttribute
    private BigDecimal time;

    @XmlElementWrapper
    @XmlElement(name = "property")
    private final List<Property> properties = new ArrayList<>();

    @XmlElement
    private TestCaseResult error;

    @XmlElement(name = "testcase")
    private final List<TestCaseResult> testCaseResults = new ArrayList<>();

    /**
     * Add property.
     *
     * @param name property name.
     * @param value property value.
     */
    public void addProperty(String name, String value) {
        Property property = factory.createProperty(name, value);
        synchronized (properties) {
            properties.add(property);
        }
    }

    /**
     * Set error when the test class failed to load
     *
     * @param error error result instance.
     */
    public void setError(TestCaseResult error) {
        this.error = error;
    }

    /**
     * Add TestCaseResult instance.
     *
     * @param caseResult TestCaseResult instatnce.
     */
    public void addTestCaseResult(TestCaseResult caseResult) {
        synchronized (testCaseResults) {
            testCaseResults.add(caseResult);
        }
    }

    /**
     * Get test count.
     *
     * @return test count.
     */
    @XmlAttribute
    public int getTests() {
        return testCaseResults.size();
    }

    /**
     * Get failure count.
     *
     * @return failure count.
     */
    @XmlAttribute
    public int getFailures() {
        int failures = 0;
        for (TestCaseResult caseResult : testCaseResults)
            failures += caseResult.getFailures();
        return failures;
    }

    /**
     * Get error count.
     *
     * @return error count.
     */
    @XmlAttribute
    public int getErrors() {
        int errors = 0;
        for (TestCaseResult caseResult : testCaseResults)
            errors += caseResult.getErrors();
        return errors;
    }

    /**
     * Get skipped count.
     *
     * @return skipped count.
     */
    @XmlAttribute
    public int getSkipped() {
        int skipped = 0;
        for (TestCaseResult caseResult : testCaseResults)
            skipped += caseResult.getSkipped();
        return skipped;
    }
}
