package jp.vmi.selenium.selenese.log;

import java.util.regex.Pattern;

/**
 * Cookie filter.
 */
public class CookieFilter {

    /**
     * All cookies pass through.
     */
    public static final CookieFilter ALL_PASS = new CookieFilter(null, null);

    /**
     * Type of cookie filter.
     */
    public static enum FilterType {
        /** pass cookies matched pattern. */
        PASS(false),
        /** skip cookies matched pattern. */
        SKIP(true);

        private final boolean isInverse;

        private FilterType(boolean isInverse) {
            this.isInverse = isInverse;
        }
    }

    final FilterType filterType;
    final Pattern pattern;

    /**
     * Constructor.
     *
     * @param filterType type of cookie filter.
     * @param pattern regex pattern.
     */
    public CookieFilter(FilterType filterType, String pattern) {
        this.filterType = filterType;
        this.pattern = (pattern != null) ? Pattern.compile(pattern) : null;
    }

    /**
     * Test name passes through this filter.
     *
     * @param name cookie name.
     *
     * @return true if the name passes through this filter.
     */
    public boolean isPass(String name) {
        return pattern != null ? pattern.matcher(name).find() ^ filterType.isInverse : true;
    }
}
