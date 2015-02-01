package jp.vmi.selenium.selenese.utils;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Date and time utilities.
 */
public final class DateTimeUtils {

    private static FastDateFormat formatWithMS;
    private static FastDateFormat formatWithoutMS;
    private static FastDateFormat formatTimeWithMS;

    static {
        setFormat("yyyy-MM-dd", " ", "HH:mm:ss", ".SSS", " ", "ZZ");
    }

    /**
     * Set date and time format.
     *
     * @param ymd format of year, month and day.
     * @param sep1 separator between ymd and hms.
     * @param hms format of hour, minulte and second.
     * @param ms format of millisecond.
     * @param sep2 separator between hms and tz.
     * @param tz timezone.
     */
    public static void setFormat(String ymd, String sep1, String hms, String ms, String sep2, String tz) {
        formatWithMS = FastDateFormat.getInstance(ymd + sep1 + hms + ms + sep2 + tz);
        formatWithoutMS = FastDateFormat.getInstance(ymd + sep1 + hms + sep2 + tz);
        formatTimeWithMS = FastDateFormat.getInstance(hms + ms);
    }

    /**
     * Format date time with milli secconds.
     *
     * @param time time of UTC.
     * @return formatted date time with milli seconds.
     */
    public static String formatWithMS(long time) {
        return formatWithMS.format(time);
    }

    /**
     * Format date time without milli secconds.
     *
     * @param time time of UTC.
     * @return formatted date time without milli seconds.
     */
    public static String formatWithoutMS(long time) {
        return formatWithoutMS.format(time);
    }

    /**
     * Format time (hour, minute, second and millisecond).
     *
     * @param time time of UTC.
     * @return formatted time.
     */
    public static String formatTimeWithMS(long time) {
        return formatTimeWithMS.format(time);
    }
}
