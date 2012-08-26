package jp.vmi.junit.result;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Test result interface. (for TestResult map in JUnitResult)
 */
public abstract class TestResult {

    private String name;

    private long startTimeOfUTC;

    private long startTime;

    private long endTime;

    protected TestResult() {
        startTest();
    }

    protected TestResult(String name) {
        this.name = name;
        startTest();
    }

    private void startTest() {
        this.endTime = this.startTime = System.nanoTime();
        this.startTimeOfUTC = System.currentTimeMillis();
    }

    protected void endTest() {
        this.endTime = System.nanoTime();
    }

    /**
     * Get test name.
     *
     * @return test name.
     */
    @XmlAttribute
    public String getName() {
        return name;
    }

    /**
     * Get timestamp.
     *
     * @return start timestamp.
     */
    @XmlAttribute
    public String getTimestamp() {
        return DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(startTimeOfUTC);
    }

    /**
     * Get duration time of test execution.
     *
     * @return time
     */
    @XmlAttribute
    public String getTime() {
        return String.format("%.3f", (endTime - startTime) / 1000000000.0 /* ns -> sec */);
    }
}
