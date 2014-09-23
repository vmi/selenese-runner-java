package jp.vmi.selenium.selenese.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.ui.Duration;

/**
 * Record log message.
 */
public class StopWatch {

    private long startTimeOfUTC;

    private long endTimeOfUTC;

    private long startTime;

    private long endTime;

    /**
     * Start time.
     */
    public void start() {
        start(System.nanoTime(), System.currentTimeMillis());
    }

    /**
     * Start time.
     *
     * @param nanoTime System.nanoTime()
     * @param currentTimeMillis System.currentTimeMillis()
     */
    public void start(long nanoTime, long currentTimeMillis) {
        startTime = endTime = nanoTime;
        startTimeOfUTC = endTimeOfUTC = currentTimeMillis;
    }

    /**
     * End time.
     */
    public void end() {
        end(System.nanoTime(), System.currentTimeMillis());
    }

    /**
     * End time.
     *
     * @param nanoTime System.nanoTime()
     * @param currentTimeMillis System.currentTimeMillis()
     */
    public void end(long nanoTime, long currentTimeMillis) {
        endTime = nanoTime;
        endTimeOfUTC = currentTimeMillis;
    }

    /**
     * Get start time of UTC as milli second.
     *
     * @return start time.
     */
    public long getStartTimeOfUTC() {
        return startTimeOfUTC;
    }

    /**
     * Get end time of UTC as milli second.
     *
     * @return end time.
     */
    public long getEndTimeOfUTC() {
        return endTimeOfUTC;
    }

    /**
     * Get nano-second of duration.
     *
     * @return duration (ns)
     */
    public long getDurationNanoSec() {
        return endTime - startTime;
    }

    /**
     * Get human readable duration string.
     *
     * @return duration string.
     */
    public String getDurationString() {
        StringBuilder ds = new StringBuilder();
        Duration d = new Duration(endTime - startTime, TimeUnit.NANOSECONDS);
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
        return new Date(startTimeOfUTC).toString();
    }

    /**
     * Get human readable end time string.
     * 
     * @return end time string
     */

    public String getEndTimeString() {
        return new Date(endTimeOfUTC).toString();
    }
}
