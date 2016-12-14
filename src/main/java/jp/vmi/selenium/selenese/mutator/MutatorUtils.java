package jp.vmi.selenium.selenese.mutator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for mutator.
 */
public final class MutatorUtils {

    private MutatorUtils() {
        // no operation.
    }

    private static final Pattern RE = Pattern.compile("\\G\\s*(?:(\\w+)|(\\W))");

    /**
     * Generate the pattern for testing presence of the code.
     *
     * @param code code.
     * @return pattern for testing.
     */
    public static Pattern generatePatternForCodePresence(String code) {
        StringBuilder pattern = new StringBuilder(code.length() * 2);
        Matcher matcher = RE.matcher(code);
        while (matcher.find()) {
            if (pattern.length() != 0)
                pattern.append("\\s*");
            String symbol = matcher.group(1);
            if (symbol != null)
                pattern.append(symbol);
            else
                pattern.append(Pattern.quote(matcher.group(2)));
        }
        return Pattern.compile(pattern.toString());
    }
}
