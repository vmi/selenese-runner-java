package jp.vmi.selenium.selenese.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.ui.Duration;

/**
 * Record log message.
 */
public class StopWatch {

    private long startTime;

    private long endTime;

    /**
     * Start time.
     */
    public void start() {
        start(System.currentTimeMillis());
    }

    /**
     * Start time.
     *
     * @param currentTimeMillis System.currentTimeMillis()
     */
    public void start(long currentTimeMillis) {
        startTime = endTime = currentTimeMillis;
    }

    /**
     * End time.
     */
    public void end() {
        end(System.currentTimeMillis());
    }

    /**
     * End time.
     *
     * @param currentTimeMillis System.currentTimeMillis()
     */
    public void end(long currentTimeMillis) {
        endTime = currentTimeMillis;
    }

    /**
     * Get start time of UTC as milli second.
     *
     * @return start time.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Get end time of UTC as milli second.
     *
     * @return end time.
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Get milli-second of duration.
     *
     * @return duration (ms)
     */
    public long getDuration() {
        return endTime - startTime;
    }

    /**
     * Get human readable duration string.
     *
     * @return duration string.
     */
    public String getDurationString() {
        StringBuilder ds = new StringBuilder();
        Duration d = new Duration(endTime - startTime, TimeUnit.MILLISECONDS);
        long h = d.in(TimeUnit.HOURS);
        if (h > 0)
            ds.append(h).append("hour");
        long m = d.in(TimeUnit.MINUTES) % 60;
        if (ds.length() > 0)
            ds.append('/').append(m).append("min");
        else if (m > 0)
            ds.append(m).append("min");
        long s = d.in(TimeUnit.SECONDS) % 60;
        double ms = (d.in(TimeUnit.MILLISECONDS) % 1000) / 1000.0;
        if (ds.length() > 0)
            ds.append('/');
        ds.append(String.format("%.3fsec", s + ms));
        return ds.toString();
    }

    /**
     * Get human readable start time string.
     *
     * @return start time string
     */

    public String getStartTimeString() {
        return DateTimeUtils.formatWithMS(startTime);
    }

    /**
     * Get human readable end time string.
     *
     * @return end time string
     */

    public String getEndTimeString() {
        return DateTimeUtils.formatWithMS(endTime);
    }
}
