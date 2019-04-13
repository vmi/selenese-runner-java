package jp.vmi.selenium.selenese.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Date and time utilities.
 */
public final class DateTimeUtils {

    private static DateTimeFormatter formatWithMS;
    private static DateTimeFormatter formatWithoutMS;
    private static DateTimeFormatter formatTimeWithMS;
    private static final DateTimeFormatter formatIso8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    static {
        setFormat("yyyy-MM-dd", " ", "HH:mm:ss", ".SSS", " ", "XXX");
    }

    private DateTimeUtils() {
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
        formatWithMS = DateTimeFormatter.ofPattern(ymd + sep1 + hms + ms + sep2 + tz);
        formatWithoutMS = DateTimeFormatter.ofPattern(ymd + sep1 + hms + sep2 + tz);
        formatTimeWithMS = DateTimeFormatter.ofPattern(hms + ms);
    }

    private static ZonedDateTime toZonedDateTime(long time) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    /**
     * Format date time with milli secconds.
     *
     * @param time time of UTC.
     * @return formatted date time with milli seconds.
     */
    public static String formatWithMS(long time) {
        return formatWithMS.format(toZonedDateTime(time));
    }

    /**
     * Format date time without milli secconds.
     *
     * @param time time of UTC.
     * @return formatted date time without milli seconds.
     */
    public static String formatWithoutMS(long time) {
        return formatWithoutMS.format(toZonedDateTime(time));
    }

    /**
     * Format time (hour, minute, second and millisecond).
     *
     * @param time time of UTC.
     * @return formatted time.
     */
    public static String formatTimeWithMS(long time) {
        return formatTimeWithMS.format(toZonedDateTime(time));
    }

    /**
     * Format time as ISO8601.
     *
     * "yyyy-MM-dd'T'HH:mm:ssXXX" to "2019-04-13T14:19:46+09:00".
     *
     * @param time time of UTC.
     * @return formatted time.
     */
    public static String formatIso8601(long time) {
        return formatIso8601.format(toZonedDateTime(time));
    }

    /**
     * Parse time string as ISO8601.
     *
     * parse "2019-04-13T14:19:46+09:00".
     *
     * @param timeStr string of time.
     * @return parsed time.
     */
    public static TemporalAccessor parseIso8601(String timeStr) {
        return formatIso8601.parse(timeStr);
    }
}
