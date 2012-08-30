package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * OjbectFactory for JAXB.
 */
@XmlRegistry
public class ObjectFactory {

    static final ObjectFactory factory = new ObjectFactory();

    /**
     * Create TestCaseResult instance.
     *
     * @return TestCaseResult instance.
     */
    public TestCaseResult createTestCaseResult() {
        return new TestCaseResult();
    }

    /**
     * Create TestCaseResult instance.
     *
     * @param name test-case name.
     * @return TestCaseResult instance.
     */
    public TestCaseResult createTestCaseResult(String name) {
        TestCaseResult tcr = createTestCaseResult();
        tcr.setName(name);
        return tcr;
    }

    /**
     * Create TestSuiteResult instance.
     *
     * @return TestSuiteResult instance.
     */
    public TestSuiteResult createTestSuiteResult() {
        return new TestSuiteResult();
    }

    /**
     * Create TestSuiteResult.
     *
     * @param name test-suite name.
     * @return TestSuiteResult.
     */
    public TestSuiteResult createTestSuiteResult(String name) {
        TestSuiteResult tsr = createTestSuiteResult();
        tsr.setName(name);
        return tsr;
    }

    /**
     * Create Property instance.
     *
     * @return Property instance.
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create Property instance.
     *
     * @param name property name.
     * @param value property value.
     * @return Property instance.
     */
    public Property createProperty(String name, String value) {
        Property p = new Property();
        p.setName(name);
        p.setValue(value);
        return p;
    }

    /**
     * Create Failure instance.
     *
     * @return Failure instance.
     */
    public Failure createFailure() {
        return new Failure();
    }

    /**
     * Create Failure instance.
     *
     * @param message failure message.
     * @param value failure value.
     * @return Failure instance.
     */
    public Failure createFailure(String message, String value) {
        Failure f = new Failure();
        f.setMessage(message);
        f.setValue(value);
        return f;
    }

    /**
     * Create Error instance.
     *
     * @return Error instance.
     */
    public Error createError() {
        return new Error();
    }

    /**
     * Create Error instance.
     *
     * @param message error message.
     * @param value error value.
     * @return Error instance.
     */
    public Error createError(String message, String value) {
        Error e = new Error();
        e.setMessage(message);
        e.setValue(value);
        return e;
    }
}
