package jp.vmi.selenium.selenese.utils;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.support.ui.Duration;

public final class LoggerUtils {

    private LoggerUtils() {
    }

    public static String durationToString(long stime, long etime) {
        StringBuilder ds = new StringBuilder();
        Duration d = new Duration(etime - stime, TimeUnit.NANOSECONDS);
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
        ds.append(String.format("%.1fsec", s + ms));
        return ds.toString();
    }

    public static String quote(String str) {
        return "\"" + str.replaceAll("([\\\\\"])", "\\\\$1") + "\"";
    }

    public static String[] quote(String[] strs) {
        int len = strs.length;
        String[] result = new String[len];
        for (int i = 0; i < len; i++)
            result[i] = quote(strs[i]);
        return result;
    }
}
