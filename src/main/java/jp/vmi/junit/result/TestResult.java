package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Test result interface. (for TestResult map in JUnitResult)
 *
 * @param <T> test target type.
 */
public abstract class TestResult<T extends ITestTarget> {

    protected T testTarget;

    /**
     * Set test target.
     *
     * @param testTarget test target.
     */
    public void setTestTarget(T testTarget) {
        this.testTarget = testTarget;
    }

    /**
     * Get test file base name.
     *
     * @return test file base name.
     */
    @XmlTransient
    public String getBaseName() {
        return testTarget.getBaseName();
    }

    /**
     * Get test name.
     *
     * @return test name.
     */
    @XmlAttribute
    public String getName() {
        return testTarget.getName();
    }

    /**
     * Get timestamp.
     *
     * @return start timestamp.
     */
    @XmlAttribute
    public String getTimestamp() {
        return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(testTarget.getStopWatch().getStartTime());
    }

    /**
     * Get duration time of test execution.
     *
     * @return time
     */
    @XmlAttribute
    public String getTime() {
        return String.format("%.3f", testTarget.getStopWatch().getDuration() / 1000.0 /* ms -> sec */);
    }
}
