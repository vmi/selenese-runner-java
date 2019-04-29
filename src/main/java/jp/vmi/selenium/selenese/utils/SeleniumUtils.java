package jp.vmi.selenium.selenese.utils;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for Selenium.
 */
public class SeleniumUtils {

    private static final DecimalFormatSymbols US_FORMAT = DecimalFormatSymbols.getInstance(Locale.US);

    private static final Logger log = LoggerFactory.getLogger(SeleniumUtils.class);

    private SeleniumUtils() {
    }

    /**
     * string-matching pattern of SeleniumIDE.
     */
    public static class SeleniumPattern {

        @SuppressWarnings("javadoc")
        public static enum Type {
            REGEXP, REGEXPI, GLOB, EXACT
        }

        /**
         * Type of string-matching pattern.
         */
        public final Type type;

        /**
         * Regular Expression of pattern.
         */
        public final Pattern regexpPattern;

        /**
         * String of pattern.
         */
        public final String stringPattern;

        /**
         * Constructor.
         *
         * @param pattern string-matching pattern.
         */
        public SeleniumPattern(String pattern) {
            String[] p = pattern.split(":", 2);
            if (p.length == 2) {
                String type = p[0].toLowerCase();
                if ("regexp".equals(type)) {
                    this.type = Type.REGEXP;
                    this.regexpPattern = Pattern.compile(p[1]);
                    this.stringPattern = p[1];
                    return;
                } else if ("regexpi".equals(type)) {
                    this.type = Type.REGEXPI;
                    this.regexpPattern = Pattern.compile(p[1], Pattern.CASE_INSENSITIVE);
                    this.stringPattern = p[1];
                    return;
                } else if ("glob".equals(type)) {
                    pattern = p[1];
                    // don't return here.
                } else if ("exact".equals(type)) {
                    this.type = Type.EXACT;
                    this.regexpPattern = null;
                    this.stringPattern = p[1];
                    return;
                }
            }
            if (pattern.indexOf('*') >= 0 || pattern.indexOf('?') >= 0) {
                this.type = Type.GLOB;
                // see http://stackoverflow.com/a/3619098
                StringBuilder re = new StringBuilder("\\A\\Q");
                re.append(pattern.replace("*", "\\E.*\\Q").replace("?", "\\E.\\Q"));
                if (re.length() >= 6 && re.charAt(4) == '\\' && re.charAt(5) == 'E')
                    re.delete(2, 6);
                int len = re.length();
                if (re.charAt(len - 2) == '\\' && re.charAt(len - 1) == 'Q')
                    re.setCharAt(re.length() - 1, 'z');
                else
                    re.append("\\E\\z");
                this.regexpPattern = Pattern.compile(re.toString(), Pattern.DOTALL);
            } else {
                this.type = Type.EXACT;
                this.regexpPattern = null;
            }
            this.stringPattern = pattern;
        }

        /**
         * Match pattern.
         *
         * @param input input string.
         * @return true if matched.
         */
        public boolean matches(String input) {
            input = normalizeSpaces(input);
            switch (type) {
            case REGEXP:
            case REGEXPI:
            case GLOB:
                return regexpPattern.matcher(input).find();
            case EXACT:
                return normalizeSpaces(stringPattern).equals(input);
            default:
                throw new UnsupportedOperationException(type.toString());
            }
        }

        @Override
        public String toString() {
            return "SeleniumPattern[" + type + ":" + StringEscapeUtils.escapeJava(stringPattern) + "]";
        }
    }

    /**
     * String-match pattern.
     *
     * @param pattern pattern. prefix is "glob:", "regexp:", "regexpi:", or "exact:".
     * @param input input string.
     * @return true if matched pattern.
     */
    public static boolean patternMatches(String pattern, CharSequence input) {
        return new SeleniumPattern(pattern).matches(input.toString());
    }

    /**
     * Unifty U+0020 and U+00A0, Trim, and compress spaces in string.
     *
     * @param str string.
     * @return normalized string.
     */
    public static String normalizeSpaces(String str) {
        int si = str.indexOf(' ');
        int ni = str.indexOf('\u00A0');
        if (si < 0 && ni < 0)
            return str;
        int len = str.length();
        StringBuilder buf = new StringBuilder(len);
        boolean skipSpc = true;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c != ' ' && c != '\u00A0') {
                buf.append(c);
                skipSpc = false;
            } else if (!skipSpc) {
                buf.append(' ');
                skipSpc = true;
            }
            // skip if skipSpc && (c == ' ' || c == '\u00A0)
        }
        int blen = buf.length();
        if (blen > 0 && buf.charAt(blen - 1) == ' ')
            buf.deleteCharAt(blen - 1);
        return buf.toString();
    }

    /**
     * Stringify the double value.
     *
     * If the double value is mathematically an integer, drop fraction part.
     *
     * @param d double value.
     * @return stringified double value.
     */
    public static String doubleToString(double d) {
        if (d % 1.0 == 0)
            return new DecimalFormat("0", US_FORMAT).format(d);
        else
            return Double.toString(d);
    }

    /**
     * Convert to String from the result of execute().
     *
     * @param <T> the type of result object.
     * @param result the result of execute().
     * @return converted string.
     */
    public static <T> String convertToString(T result) {
        if (result == null)
            return "";
        else if (result instanceof Object[])
            return LangUtils.join(",", Arrays.stream((Object[]) result));
        else if (result instanceof Iterable)
            return LangUtils.join(",", ((Iterable<?>) result).iterator());
        else if (result instanceof Iterator)
            return LangUtils.join(",", (Iterator<?>) result);
        else if (result instanceof Double)
            return doubleToString((Double) result);
        else
            return result.toString();
    }

    /**
     * Check if Exception type is "element not found".
     *
     * @param e RuntimeException
     * @return true if the exception is "element not found".
     */
    public static boolean isElementNotFound(RuntimeException e) {
        return e instanceof NotFoundException
            || e instanceof StaleElementReferenceException
            || e.getCause() instanceof NotFoundException
            || e.getCause() instanceof StaleElementReferenceException;
    }

    private static Rectangle getWindowSafeBoundsFromEnvVars() {
        String widthStr = System.getenv("SCREEN_WIDTH");
        String heightStr = System.getenv("SCREEN_HEIGHT");
        if (widthStr == null || heightStr == null)
            return null;
        try {
            int width = Integer.parseInt(widthStr);
            int height = Integer.parseInt(heightStr);
            log.warn("Get screen size from environment variables: SCREEN_WIDTH and SCREEN_HEIGHT");
            return new Rectangle(0, 0, width, height);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Rectangle getWindowSafeBoundsFromGraphEnv() {
        // https://stackoverflow.com/questions/32555329/java-get-maximized-state-window-size
        GraphicsEnvironment gc = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (gc.isHeadlessInstance())
            return null;
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());
        int x = bounds.x + insets.left;
        int y = bounds.y + insets.top;
        int width = bounds.width - (insets.left + insets.right);
        int height = bounds.height - (insets.top + insets.bottom);
        log.warn("Get screen size from grahics environment.");
        return new Rectangle(x, y, width, height);
    }

    /**
     * Maximize window with workaround.
     *
     * @param driver WebDriver instance.
     */
    public static void windowMaximize(WebDriver driver) {
        Window window = driver.manage().window();
        try {
            window.maximize();
        } catch (WebDriverException e) {
            String message = e.getMessage();
            if (!message.contains("failed to change window state to maximized"))
                throw e;
            log.warn("Can't maximize: {}", message);
            Rectangle bounds = getWindowSafeBoundsFromEnvVars();
            if (bounds == null)
                bounds = getWindowSafeBoundsFromGraphEnv();
            if (bounds == null)
                throw e;
            Point position = new Point(bounds.x, bounds.y);
            Dimension size = new Dimension(bounds.width, bounds.height);
            window.setPosition(position);
            window.setSize(size);
            log.warn("Window changed: (x, y) = {}, (width, height) = {}", position, size);
        }
    }
}
