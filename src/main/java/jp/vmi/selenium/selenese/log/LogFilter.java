package jp.vmi.selenium.selenese.log;

import java.util.EnumSet;

import jp.vmi.selenium.selenese.config.IConfig;

/**
 * Log filter types.
 */
public enum LogFilter {
    /** enable cookie logging. */
    COOKIE,
    /** enable title logging. */
    TITLE,
    /** enable URL logging. */
    URL,
    ;

    /**
     * Disable all page information logging.
     *
     * @return empty set of LogFilter.
     */
    public static EnumSet<LogFilter> none() {
        return EnumSet.noneOf(LogFilter.class);
    }

    /**
     * Enable all page information logging.
     *
     * @return full set of LogFilter.
     */
    public static EnumSet<LogFilter> all() {
        return EnumSet.allOf(LogFilter.class);
    }

    /**
     * Parse command line arguments for "--log-filter".
     *
     * @param logFilter log filter.
     * @param args command line arguments.
     */
    public static void parse(EnumSet<LogFilter> logFilter, String[] args) {
        for (String arg : args) {
            if (arg.isEmpty())
                throw new IllegalArgumentException("Invalid value for --" + IConfig.LOG_FILTER + " is empty.");
            boolean add;
            switch (arg.charAt(0)) {
            case '+':
                add = true;
                break;
            case '-':
                add = false;
                break;
            default:
                throw new IllegalArgumentException("Invalid value for --" + IConfig.LOG_FILTER + ": " + arg);
            }
            switch (arg.substring(1)) {
            case "pageinfo":
                if (add) {
                    logFilter.add(COOKIE);
                    logFilter.add(TITLE);
                    logFilter.add(URL);
                } else {
                    logFilter.remove(COOKIE);
                    logFilter.remove(TITLE);
                    logFilter.remove(URL);
                }
                break;
            case "cookie":
                if (add)
                    logFilter.add(COOKIE);
                else
                    logFilter.remove(COOKIE);
                break;
            case "title":
                if (add)
                    logFilter.add(TITLE);
                else
                    logFilter.remove(TITLE);
                break;
            case "url":
                if (add)
                    logFilter.add(URL);
                else
                    logFilter.remove(URL);
                break;
            default:
                throw new IllegalArgumentException("Invalid value for --" + IConfig.LOG_FILTER + ": " + arg);
            }
        }
    }
}
