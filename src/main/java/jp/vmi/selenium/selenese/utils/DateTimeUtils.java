package jp.vmi.selenium.selenese.utils;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Date and time utilities.
 */
public final class DateTimeUtils {

    private static String defaultWithMS = "yyyy-MM-dd HH:mm:ss.SSS ZZ";
    private static String defaultWithoutMS = "yyyy-MM-dd HH:mm:ss ZZ";
    private static FastDateFormat formatWithMS = FastDateFormat.getInstance(defaultWithMS);
    private static FastDateFormat formatWithoutMS = FastDateFormat.getInstance(defaultWithoutMS);

    /**
     * Set date and time format.
     *
     * @param withMS date and time format with milli secconds. (= years to milli seconds)
     * @param withoutMS date and time format without milli seconds. (= years to seconds)
     */
    public static void setFormat(String withMS, String withoutMS) {
        formatWithMS = FastDateFormat.getInstance(withMS);
        formatWithoutMS = FastDateFormat.getInstance(withoutMS);
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
}
