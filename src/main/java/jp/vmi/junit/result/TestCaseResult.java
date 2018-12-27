package jp.vmi.junit.result;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

import jp.vmi.junit.result.ITestTarget.Lifecycle;
import jp.vmi.selenium.selenese.utils.LogRecorder.LogMessage;

import static jp.vmi.junit.result.ObjectFactory.*;

/**
 * testcase element.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "skipped",
    "error",
    "failure",
    "systemOut",
    "systemErr"
})
public class TestCaseResult extends TestResult<ITestCase> {

    private static final String NL = System.getProperty("line.separator");

    @XmlTransient
    private boolean success = false;

    @XmlElement
    private Error error = null;

    @XmlElement
    private Failure failure = null;

    /**
     * Set success result.
     */
    public void setSuccess() {
        if (this.testTarget.getLifecycle() != Lifecycle.DRAFT)
            this.success = true;
    }

    /**
     * Set error result.
     *
     * @param message error message.
     * @param value error value.
     */
    public void setError(String message, String value) {
        if (this.testTarget.getLifecycle() != Lifecycle.DRAFT)
            error = factory.createError(message, value);
    }

    /**
     * Set failure result.
     *
     * @param message failure message.
     * @param value failure value.
     */
    public void setFailure(String message, String value) {
        if (this.testTarget.getLifecycle() != Lifecycle.DRAFT)
            failure = factory.createFailure(message, value);
    }

    /**
     *  Get error count.
     *
     *  @return error count.
     */
    public int getErrors() {
        return error != null ? 1 : 0;
    }

    /**
     *  Get failure count.
     *
     *  @return failure count.
     */
    public int getFailures() {
        return failure != null ? 1 : 0;
    }

    /**
     * Get skipped count.
     *
     * @return skipped count.
     */
    @XmlElementRef
    @XmlJavaTypeAdapter(SkippedAdapter.class)
    public Integer getSkipped() {
        return ((this.testTarget.getLifecycle() == Lifecycle.DRAFT) || (!success && error == null && failure == null)) ? 1 : 0;
    }

    /**
     * Get system-out message.
     *
     * @return system-out message.
     */
    @XmlElement(name = "system-out")
    public String getSystemOut() {
        List<LogMessage> msgs = testTarget.getLogRecorder().getMessages();
        return (msgs.size() != 0) ? StringUtils.join(msgs, NL) : null;
    }

    /**
     * Get system-err message.
     *
     * @return system-err message.
     */
    @XmlElement(name = "system-err")
    public String getSystemErr() {
        List<LogMessage> msgs = testTarget.getLogRecorder().getErrorMessages();
        return (msgs.size() != 0) ? StringUtils.join(msgs, NL) : null;
    }
}
